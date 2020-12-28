package test;

public class Neighbors
{
	private Chunk[][][] neighbors;
	
	public Neighbors(Chunk[][][] neighbors)
	{
		this.neighbors = neighbors;
	}
	
	public Neighbors(
			Chunk ftl, Chunk ftm, Chunk ftr, Chunk fml, Chunk fmm, Chunk fmr, Chunk fbl, Chunk fbm, Chunk fbr,
			Chunk mtl, Chunk mtm, Chunk mtr, Chunk mml,            Chunk mmr, Chunk mbl, Chunk mbm, Chunk mbr,
			Chunk btl, Chunk btm, Chunk btr, Chunk bml, Chunk bmm, Chunk bmr, Chunk bbl, Chunk bbm, Chunk bbr)
	{
		
	}
	
	public Chunk neighbors(int x, int y, int z)
	{
		return neighbors[z][y][x];
	}
}
