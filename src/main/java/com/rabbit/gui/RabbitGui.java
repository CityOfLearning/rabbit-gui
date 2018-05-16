package com.rabbit.gui;

import org.apache.logging.log4j.Logger;

import com.rabbit.gui.proxy.Proxy;
import com.rabbit.gui.reference.MetaData;
import com.rabbit.gui.reference.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//does this need to be its own mod? we can probably just have the code live with the others
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class RabbitGui {

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	public static Logger logger;

	@Mod.Metadata(Reference.MOD_ID)
	public ModMetadata metadata;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		RabbitGui.proxy.init();
	}

	@Mod.EventHandler
	public void postLoad(FMLPostInitializationEvent event) {
		metadata = MetaData.init(metadata);
		RabbitGui.proxy.postInit();
		RabbitGui.logger.info("Rabbit Gui has been successfully initialized");

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		RabbitGui.logger = event.getModLog();
		RabbitGui.proxy.preInit();
	}

}
