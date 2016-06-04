package com.rabbit.gui.component;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Represents gui element which contains and displays multiple objects
 *
 * @param <T>
 *            - type of contained objects
 */

@SideOnly(Side.CLIENT)
public interface WidgetList<T> {

	public WidgetList<T> add(T object);

	@SuppressWarnings("unchecked")
	public WidgetList<T> addAll(T... objects);

	public WidgetList<T> clear();

	public Object getContent();

	public WidgetList<T> remove(T object);
}
