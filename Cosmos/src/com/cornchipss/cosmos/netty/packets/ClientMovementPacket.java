package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.physx.Movement;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerPlayer;

public class ClientMovementPacket extends Packet
{
	private Movement movement;
	
	public ClientMovementPacket()
	{
		
	}
	
	private ClientMovementPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public ClientMovementPacket(byte[] buffer, int bufferOffset, Movement movement)
	{
		super(buffer, bufferOffset);
		
		this.movement = movement;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		writeInt(movement.code());
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, 
			ClientConnection client, CosmosNettyServer server)
	{
		ClientMovementPacket packet = new ClientMovementPacket(data, offset);
		
		int movement = packet.readInt();
		
		ServerPlayer player = server.players().player(client);
		
		player.movement(Movement.movementFromCode(movement));
	}
	
	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		// never will be
	}
}
