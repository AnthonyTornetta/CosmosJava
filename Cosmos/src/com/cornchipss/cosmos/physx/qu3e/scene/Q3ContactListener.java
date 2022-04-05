package com.cornchipss.cosmos.physx.qu3e.scene;

import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactConstraint;

public interface Q3ContactListener
{
	public void beginContact(Q3ContactConstraint contact);
	public void endContact(Q3ContactConstraint contact);
}
