package com.cornchipss.cosmos.systems;

import com.cornchipss.cosmos.netty.action.PlayerAction;

public interface IPlayerActionReceiver
{
	public void receiveAction(PlayerAction action);
}
