package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.utils.Logger;

public class StatusPacket extends Packet
{
	private int code;
	private String msg;
	
	public StatusPacket () {}
	
	public StatusPacket(int code)
	{
		this(code, null);
	}
	
	public StatusPacket(int code, String msg)
	{
		this.code = code;
		this.msg = msg;
	}
	
	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		if(code == 200)
			Logger.LOGGER.info(this);
		else
			Logger.LOGGER.warning(this);
	}
	
	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		if(code == 200)
			Logger.LOGGER.info(this);
		else
			Logger.LOGGER.warning(this);
	}
	
	@Override
	public String toString()
	{
		return "Status: " + code + (msg != null ? " - " + msg : "");
	}
	
	public int code()
	{
		return code;
	}
	
	public String message()
	{
		return msg;
	}
}
