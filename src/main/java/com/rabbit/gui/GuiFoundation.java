package com.rabbit.gui;

import com.rabbit.gui.proxy.Proxy;
import com.rabbit.gui.reference.MetaData;
import com.rabbit.gui.reference.Reference;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

//does this need to be its own mod? we can probably just have the code live with the others
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class GuiFoundation {

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.Metadata(Reference.MOD_ID)
	public ModMetadata metadata;

	@Mod.EventHandler
	public void postLoad(FMLPostInitializationEvent event) {
		metadata = MetaData.init(metadata);
		proxy.init();
		FMLLog.info("Rabbit Gui has been successfully initialized");
	}

}
