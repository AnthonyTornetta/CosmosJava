package com.cornchipss.cosmos.physx.qu3e.broadphase;

import java.util.Arrays;

import org.joml.AABBf;

import com.cornchipss.cosmos.physx.qu3e.collision.Q3Box;
import com.cornchipss.cosmos.physx.qu3e.dynamics.IHasTreeCallback;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3ContactManager;

public class Q3BroadPhase implements IHasTreeCallback
{
	private Q3ContactManager m_manager;

	private Q3ContactPair[] m_pairBuffer;
	private int m_pairCount;
	private int m_pairCapacity;

	private int[] m_moveBuffer;
	private int m_moveCount;
	private int m_moveCapacity;

	public Q3DynamicAABBTree m_tree;
	private int m_currentIndex;

	private void BufferMove(int id)
	{
		if(m_moveCount == m_moveCapacity)
		{
			int[] oldBuffer = m_moveBuffer;
			m_moveCapacity *= 2;
			m_moveBuffer = new int[m_moveCapacity];
			
			System.arraycopy(oldBuffer, 0, m_moveBuffer, 0, oldBuffer.length);
		}
		
		m_moveBuffer[m_moveCount++] = id;
	}

	@Override
	public boolean TreeCallBack(int index)
	{
		if(index == m_currentIndex)
			return true;
		
		if(m_pairCount == m_pairCapacity)
		{
			Q3ContactPair[] oldBuffer = m_pairBuffer;
			
			m_pairCapacity = 2 * m_pairCapacity;
			m_pairBuffer = new Q3ContactPair[m_pairCapacity];
			
			System.arraycopy(oldBuffer, 0, m_pairBuffer, 0, oldBuffer.length);
			
			for(int i = oldBuffer.length; i < m_pairBuffer.length; i++)
			{
				m_pairBuffer[i] = new Q3ContactPair();
			}
		}
		
		int iA = Math.min(index, m_currentIndex);
		int iB = Math.max(index,  m_currentIndex);
		
		m_pairBuffer[m_pairCount].A = iA;
		m_pairBuffer[m_pairCount].B = iB;
		m_pairCount++;
		
		return true;
	}
	
	public Q3BroadPhase(Q3ContactManager manager)
	{
		this.m_manager = manager;
		
		this.m_tree = new Q3DynamicAABBTree();
		
		this.m_pairCount = 0;
		this.m_pairCapacity = 64;
		this.m_pairBuffer = new Q3ContactPair[m_pairCapacity];
		
		for(int i = 0; i < this.m_pairCapacity; i++)
		{
			m_pairBuffer[i] = new Q3ContactPair();
		}
		
		m_moveCount = 0;
		m_moveCapacity = 64;
		m_moveBuffer = new int[m_moveCapacity];
	}

	public void InsertBox(Q3Box box, AABBf aabb)
	{
		int id = m_tree.Insert(aabb, box);
		box.broadPhaseIndex = id;
		BufferMove(id);
	}

	public void RemoveBox(Q3Box shape)
	{
		m_tree.Remove(shape.broadPhaseIndex);
	}

	public void UpdatePairs()
	{
		m_pairCount = 0;
		
		for(int i = 0; i < m_moveCount; i++)
		{
			m_currentIndex = m_moveBuffer[i];
			AABBf aabb = m_tree.GetFatAABB(m_currentIndex);
			
			m_tree.Query(this, aabb);
		}
		
		m_moveCount = 0;
		
		// TODO THIS COULD BE TOTALLY WRONG
		Arrays.sort(m_pairBuffer, (lhs, rhs) -> 
		{
			if ( lhs.A < rhs.A )
				return 1;

			if ( lhs.A == rhs.A )
				return lhs.B < rhs.B ? 1 : -1;

			return -1;
		});
		
		{
			int i = 0;
			while(i < m_pairCount)
			{
				Q3ContactPair pair = m_pairBuffer[i];
				Q3Box A = (Q3Box)m_tree.GetUserData(pair.A);
				Q3Box B = (Q3Box)m_tree.GetUserData(pair.B);
				
				m_manager.AddContact(A, B);
				
				i++;
				
				// Skip duplicate pairs by iterating i until we find a unique pair
				while(i < m_pairCount)
				{
					Q3ContactPair potentialDup = m_pairBuffer[i];
					
					if(pair.A != potentialDup.A || pair.B != potentialDup.B)
					{
						break;
					}
					
					i++;
				}
			}
		}
		
		m_tree.Validate();
	}

	public void Update(int id, AABBf aabb)
	{
		if(m_tree.Update(id, aabb))
			BufferMove(id);
	}

	public boolean TestOverlap(int A, int B)
	{
		return m_tree.GetFatAABB(A).testAABB(m_tree.GetFatAABB(B));
	}
}
