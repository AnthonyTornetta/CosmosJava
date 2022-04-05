package com.cornchipss.cosmos.physx.qu3e.broadphase;

import org.joml.AABBf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.qu3e.dynamics.IHasTreeCallback;
import com.cornchipss.cosmos.physx.qu3e.geometry.Q3RaycastData;

public class Q3DynamicAABBTree
{
	private static void FattenAABB(AABBf aabb)
	{
		float k_fattener = 0.5f;

		aabb.minX -= k_fattener;
		aabb.minY -= k_fattener;
		aabb.minZ -= k_fattener;

		aabb.maxX += k_fattener;
		aabb.maxY += k_fattener;
		aabb.maxZ += k_fattener;
	}

	public Q3DynamicAABBTree()
	{
		m_root = Node.Null;

		m_capacity = 1024;
		m_count = 0;
		m_nodes = new Node[m_capacity];
		
		for(int i = 0; i < m_nodes.length; i++)
		{
			m_nodes[i] = new Node();
		}
	}

	// Provide tight-AABB
	int Insert(AABBf aabb, Object userData)
	{
		int id = AllocateNode();

		m_nodes[id].aabb = aabb;
		FattenAABB(m_nodes[id].aabb);
		m_nodes[id].userData = userData;
		m_nodes[id].height = 0;
		
		InsertLeaf(id);
		
		return id;
	}

	private void checkNodeBounds(int id)
	{
		if (id < 0 || id >= m_capacity)
			throw new IndexOutOfBoundsException(
				id + " out of bounds [0, " + m_capacity + ")");

	}

	private void checkNode(int id)
	{
		checkNodeBounds(id);
		if (!m_nodes[id].IsLeaf())
			throw new IllegalArgumentException("Node " + id + " must be leaf");
	}

	void Remove(int id)
	{
		checkNode(id);

		RemoveLeaf(id);
		DeallocateNode(id);
	}

	boolean Update(int id, AABBf aabb)
	{
		checkNode(id);

		if (m_nodes[id].aabb.testAABB(aabb))
			return false;

		RemoveLeaf(id);

		m_nodes[id].aabb = aabb;
		FattenAABB(m_nodes[id].aabb);

		InsertLeaf(id);

		return true;
	}

	public Object GetUserData(int id)
	{
		checkNodeBounds(id);

		return m_nodes[id].userData;
	}

	AABBf GetFatAABB(int id)
	{
		checkNodeBounds(id);

		return m_nodes[id].aabb;
	}
//		void Render( q3Render *render ) const;

	public void Query(IHasTreeCallback cb, AABBf aabb)
	{
		int k_stackCapacity = 256;
		int[] stack = new int[ k_stackCapacity ];
		int sp = 1;

		stack[0] = m_root;

		while ( sp != 0 )
		{
			// k_stackCapacity too small
			assert( sp < k_stackCapacity );

			int id = stack[ --sp ];

			Node n = m_nodes[id];
			if ( aabb.testAABB(n.aabb) )
			{
				if ( n.IsLeaf() )
				{
					if ( !cb.TreeCallBack( id ) )
						return;
				}
				else
				{
					stack[ sp++ ] = n.left;
					stack[ sp++ ] = n.right;
				}
			}
		}
	}

	public void Query(IHasTreeCallback cb, Q3RaycastData rayCast)
	{
		final float k_epsilon = (float)( 1.0e-6 );
		final int k_stackCapacity = 256;
		int stack[] = new int[ k_stackCapacity ];
		int sp = 1;

		stack[0] = m_root;

		Vector3f p0 = rayCast.start;
		Vector3f p1 = rayCast.dir.mul(rayCast.t, new Vector3f()).add(p0);

		while( sp != 0 )
		{
			// k_stackCapacity too small
			assert( sp < k_stackCapacity );

			int id = stack[--sp];

			if ( id == Node.Null )
				continue;

			Node n = m_nodes[id];
			
			Vector3f aabbMax = new Vector3f(n.aabb.maxX, n.aabb.maxY, n.aabb.maxZ);
			Vector3f aabbMin = new Vector3f(n.aabb.minX, n.aabb.minY, n.aabb.minZ);

			Vector3f e = aabbMax.sub(aabbMin, new Vector3f());
			Vector3f d = p1.sub(p0, new Vector3f());
			Vector3f m = p0.add(p1, new Vector3f()).sub(aabbMin).sub(aabbMax);

			float adx = Math.abs( d.x );

			if ( Math.abs( m.x ) > e.x + adx )
				continue;

			float ady = Math.abs( d.y );

			if ( Math.abs( m.y ) > e.y + ady )
				continue;

			float adz = Math.abs( d.z );

			if ( Math.abs( m.z ) > e.z + adz )
				continue;

			adx += k_epsilon;
			ady += k_epsilon;
			adz += k_epsilon;

			if( Math.abs( m.y * d.z - m.z * d.y) > e.y * adz + e.z * ady )
				continue;

			if( Math.abs( m.z * d.x - m.x * d.z) > e.x * adz + e.z * adx )
				continue;

			if ( Math.abs( m.x * d.y - m.y * d.x) > e.x * ady + e.y * adx )
				continue;

			if ( n.IsLeaf( ) )
			{
				if ( !cb.TreeCallBack( id ) )
					return;
			}

			else
			{
				stack[ sp++ ] = n.left;
				stack[ sp++ ] = n.right;
			}
		}
	}

	// For testing
	void Validate()
	{
		int freeNodes = 0;
		int index = m_freeList;

		while (index != Node.Null)
		{
			checkNode(index);
			index = m_nodes[index].parentOrNext;
			freeNodes++;
		}

		if (m_count + freeNodes != m_capacity)
		{
			throw new IllegalStateException("Something is wrong with the tree");
		}

		if (m_root != Node.Null)
		{
			if (m_nodes[m_root].parentOrNext != Node.Null)
				throw new IllegalStateException(
					"Something is wrong with the tree");

			ValidateStructure(m_root);
		}
	}

//	private:
	private static class Node
	{
		private int parentOrNext; // free list
		private int left;
		private int right;

		public Node()
		{
			right = Null;
			left = Null;
			parentOrNext = Null;
		}
		
		// Fat AABB for leafs, bounding AABB for branches
		AABBf aabb;

		boolean IsLeaf()
		{
			// The right leaf does not use the same memory as the userdata,
			// and will always be Null (no children)
			return right == Null;
		}

		// Since only leaf nodes hold userdata, we can use the
		// same memory used for left/right indices to store
		// the userdata void pointer
		Object userData;

		// leaf = 0, free nodes = -1
		int height;

		static final int Null = -1;
	};

	private int AllocateNode()
	{
		if (m_freeList == Node.Null)
		{
			m_capacity *= 2;
			Node[] newNodes = new Node[m_capacity];
			System.arraycopy(m_nodes, 0, newNodes, 0, m_nodes.length);
			
			for(int i = m_nodes.length; i < newNodes.length; i++)
			{
				newNodes[i] = new Node();
			}
			
			m_nodes = newNodes;

			AddToFreeList(m_count);
		}

		int freeNode = m_freeList;

		if(m_nodes[m_freeList] == null)
			m_nodes[m_freeList] = new Node();
		
		m_freeList = m_nodes[m_freeList].parentOrNext;
		m_nodes[freeNode].height = 0;
		m_nodes[freeNode].left = Node.Null;
		m_nodes[freeNode].right = Node.Null;
		m_nodes[freeNode].parentOrNext = Node.Null;
		m_nodes[freeNode].userData = null;
		m_count++;

		return freeNode;
	}

	private void DeallocateNode(int index)
	{
		assert( index >= 0 && index < m_capacity );

		m_nodes[ index ].parentOrNext = m_freeList;
		m_nodes[ index ].height = Node.Null;
		m_freeList = index;

		--m_count;
	}

	private static AABBf q3Combine(AABBf a, AABBf b)
	{
		AABBf c = new AABBf();

		c.minX = Math.min(a.minX, b.minX);
		c.minY = Math.min(a.minY, b.minY);
		c.minZ = Math.min(a.minZ, b.minZ);

		c.maxX = Math.max(a.maxX, b.maxX);
		c.maxY = Math.max(a.maxY, b.maxY);
		c.maxZ = Math.max(a.maxZ, b.maxZ);

		return c;
	}

	private int Balance(int iA)
	{
		Node A = m_nodes[iA];

		if (A.IsLeaf() || A.height == 1)
			return iA;

		int iB = A.left;
		int iC = A.right;
		Node B = m_nodes[iB];
		Node C = m_nodes[iC];

		int balance = C.height - B.height;

		// C is higher, promote C
		if (balance > 1)
		{
			int iF = C.left;
			int iG = C.right;
			Node F = m_nodes[iF];
			Node G = m_nodes[iG];

			// grandParent point to C
			if (A.parentOrNext != Node.Null)
			{
				if (m_nodes[A.parentOrNext].left == iA)
					m_nodes[A.parentOrNext].left = iC;

				else
					m_nodes[A.parentOrNext].right = iC;
			}
			else
				m_root = iC;

			// Swap A and C
			C.left = iA;
			C.parentOrNext = A.parentOrNext;
			A.parentOrNext = iC;

			// Finish rotation
			if (F.height > G.height)
			{
				C.right = iF;
				A.right = iG;
				G.parentOrNext = iA;
				A.aabb = q3Combine(B.aabb, G.aabb);
				C.aabb = q3Combine(A.aabb, F.aabb);

				A.height = 1 + Math.max(B.height, G.height);
				C.height = 1 + Math.max(A.height, F.height);
			}

			else
			{
				C.right = iG;
				A.right = iF;
				F.parentOrNext = iA;
				A.aabb = q3Combine(B.aabb, F.aabb);
				C.aabb = q3Combine(A.aabb, G.aabb);

				A.height = 1 + Math.max(B.height, F.height);
				C.height = 1 + Math.max(A.height, G.height);
			}

			return iC;
		}

		// B is higher, promote B
		else if (balance < -1)
		{
			int iD = B.left;
			int iE = B.right;
			Node D = m_nodes[iD];
			Node E = m_nodes[iE];

			// grandParent point to B
			if (A.parentOrNext != Node.Null)
			{
				if (m_nodes[A.parentOrNext].left == iA)
					m_nodes[A.parentOrNext].left = iB;
				else
					m_nodes[A.parentOrNext].right = iB;
			}

			else
				m_root = iB;

			// Swap A and B
			B.right = iA;
			B.parentOrNext = A.parentOrNext;
			A.parentOrNext = iB;

			// Finish rotation
			if (D.height > E.height)
			{
				B.left = iD;
				A.left = iE;
				E.parentOrNext = iA;
				A.aabb = q3Combine(C.aabb, E.aabb);
				B.aabb = q3Combine(A.aabb, D.aabb);

				A.height = 1 + Math.max(C.height, E.height);
				B.height = 1 + Math.max(A.height, D.height);
			}

			else
			{
				B.left = iE;
				A.left = iD;
				D.parentOrNext = iA;
				A.aabb = q3Combine(C.aabb, D.aabb);
				B.aabb = q3Combine(A.aabb, E.aabb);

				A.height = 1 + Math.max(C.height, D.height);
				B.height = 1 + Math.max(A.height, E.height);
			}

			return iB;
		}

		// No balancing needed
		return iA;
	}

	private static float SurfaceArea(AABBf aabb)
	{
		float x = aabb.maxX - aabb.minX;
		float y = aabb.maxY - aabb.minY;
		float z = aabb.maxZ - aabb.minZ;

		return 2.0f * (x * y + x * z + y * z);
	}

	private void InsertLeaf(int id)
	{
		if (m_root == Node.Null)
		{
			m_root = id;
			m_nodes[m_root].parentOrNext = Node.Null;
			return;
		}

		// Search for sibling
		int searchIndex = m_root;
		AABBf leafAABB = m_nodes[id].aabb;

		while (!m_nodes[searchIndex].IsLeaf())
		{
			// Cost for insertion at index (branch node), involves creation
			// of new branch to contain this index and the new leaf
			AABBf combined = q3Combine(leafAABB, m_nodes[searchIndex].aabb);
			float combinedArea = SurfaceArea(combined);
			float branchCost = 2.0f * combinedArea;

			// Inherited cost (surface area growth from heirarchy update after
			// descent)
			float inheritedCost = 2.0f
				* (combinedArea - SurfaceArea(m_nodes[searchIndex].aabb));

			int left = m_nodes[searchIndex].left;
			int right = m_nodes[searchIndex].right;

			// Calculate costs for left/right descents. If traversal is to a
			// leaf,
			// then the cost of the combind AABB represents a new branch node.
			// Otherwise
			// the cost is only the inflation of the pre-existing branch.
			float leftDescentCost;
			if (m_nodes[left].IsLeaf())
				leftDescentCost = SurfaceArea(
					q3Combine(leafAABB, m_nodes[left].aabb)) + inheritedCost;
			else
			{
				float inflated = SurfaceArea(
					q3Combine(leafAABB, m_nodes[left].aabb));
				float branchArea = SurfaceArea(m_nodes[left].aabb);
				leftDescentCost = inflated - branchArea + inheritedCost;
			}

			// Cost for right descent
			float rightDescentCost;
			if (m_nodes[right].IsLeaf())
				rightDescentCost = SurfaceArea(
					q3Combine(leafAABB, m_nodes[right].aabb)) + inheritedCost;
			else
			{
				float inflated = SurfaceArea(
					q3Combine(leafAABB, m_nodes[right].aabb));
				float branchArea = SurfaceArea(m_nodes[right].aabb);
				rightDescentCost = inflated - branchArea + inheritedCost;
			}

			// Determine traversal direction, or early out on a branch index
			if (branchCost < leftDescentCost && branchCost < rightDescentCost)
				break;

			if (leftDescentCost < rightDescentCost)
				searchIndex = left;

			else
				searchIndex = right;
		}

		int sibling = searchIndex;

		// Create new parent
		int oldParent = m_nodes[sibling].parentOrNext;
		int newParent = AllocateNode();
		m_nodes[newParent].parentOrNext = oldParent;
		m_nodes[newParent].userData = null;
		m_nodes[newParent].aabb = q3Combine(leafAABB, m_nodes[sibling].aabb);
		m_nodes[newParent].height = m_nodes[sibling].height + 1;

		// Sibling was root
		if (oldParent == Node.Null)
		{
			m_nodes[newParent].left = sibling;
			m_nodes[newParent].right = id;
			m_nodes[sibling].parentOrNext = newParent;
			m_nodes[id].parentOrNext = newParent;
			m_root = newParent;
		}

		else
		{
			if (m_nodes[oldParent].left == sibling)
				m_nodes[oldParent].left = newParent;

			else
				m_nodes[oldParent].right = newParent;

			m_nodes[newParent].left = sibling;
			m_nodes[newParent].right = id;
			m_nodes[sibling].parentOrNext = newParent;
			m_nodes[id].parentOrNext = newParent;
		}

		SyncHeirarchy(m_nodes[id].parentOrNext);
	}

	private void RemoveLeaf(int id)
	{
		if (id == m_root)
		{
			m_root = Node.Null;
			return;
		}

		// Setup parent, grandParent and sibling
		int parent = m_nodes[id].parentOrNext;
		int grandParent = m_nodes[parent].parentOrNext;
		int sibling;

		if (m_nodes[parent].left == id)
			sibling = m_nodes[parent].right;

		else
			sibling = m_nodes[parent].left;

		// Remove parent and replace with sibling
		if (grandParent != Node.Null)
		{
			// Connect grandParent to sibling
			if (m_nodes[grandParent].left == parent)
				m_nodes[grandParent].left = sibling;

			else
				m_nodes[grandParent].right = sibling;

			// Connect sibling to grandParent
			m_nodes[sibling].parentOrNext = grandParent;
		}

		// Parent was root
		else
		{
			m_root = sibling;
			m_nodes[sibling].parentOrNext = Node.Null;
		}

		DeallocateNode(parent);
		SyncHeirarchy(grandParent);
	}

	private void ValidateStructure(int index)
	{
		Node n = m_nodes[index];

		int il = n.left;
		int ir = n.right;

		if (n.IsLeaf())
		{
			assert (ir == Node.Null);
			assert (n.height == 0);

			return;
		}

		assert (il >= 0 && il < m_capacity);
		assert (ir >= 0 && ir < m_capacity);

		Node l = m_nodes[il];
		Node r = m_nodes[ir];

		assert (l.parentOrNext == index);
		assert (r.parentOrNext == index);

		ValidateStructure(il);
		ValidateStructure(ir);
	}
//		void RenderNode( q3Render *render, int index ) const;

	// Correct AABB hierarchy heights and AABBs starting at supplied
	// index traversing up the heirarchy
	private void SyncHeirarchy(int index)
	{
		while (index != Node.Null)
		{
			index = Balance(index);

			int left = m_nodes[index].left;
			int right = m_nodes[index].right;

			m_nodes[index].height = 1
				+ Math.max(m_nodes[left].height, m_nodes[right].height);
			m_nodes[index].aabb = q3Combine(m_nodes[left].aabb,
				m_nodes[right].aabb);

			index = m_nodes[index].parentOrNext;
		}
	}

	// Insert nodes at a given index until m_capacity into the free list
	private void AddToFreeList(int index)
	{
		for ( int i = index; i < m_capacity - 1; ++i)
		{
			m_nodes[ i ].parentOrNext = i + 1;
			m_nodes[ i ].height = Node.Null;
		}

		m_nodes[ m_capacity - 1 ].parentOrNext = Node.Null;
		m_nodes[ m_capacity - 1 ].height = Node.Null;
		m_freeList = index;
	}

	private int m_root;
	private Node[] m_nodes;
	private int m_count; // Number of active nodes
	private int m_capacity; // Max capacity of nodes
	private int m_freeList;
}
