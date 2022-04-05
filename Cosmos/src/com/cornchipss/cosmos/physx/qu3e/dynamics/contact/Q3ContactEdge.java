package com.cornchipss.cosmos.physx.qu3e.dynamics.contact;

import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3Body;

public class Q3ContactEdge
{
	public Q3Body other;
	public Q3ContactConstraint constraint;
	public Q3ContactEdge next;
	public Q3ContactEdge prev;
}
