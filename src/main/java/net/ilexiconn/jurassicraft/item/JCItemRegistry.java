package net.ilexiconn.jurassicraft.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ilexiconn.llibrary.IContentHandler;
import net.minecraft.item.Item;

import java.lang.reflect.Field;

public class JCItemRegistry implements IContentHandler
{
    public void init()
    {

    }

    public void initCreativeTabs()
    {

    }

    public void gameRegistry() throws Exception
    {
        initCreativeTabs();
        try
        {
            for (Field f : JCItemRegistry.class.getDeclaredFields())
            {
                Object obj = f.get(null);
                if (obj instanceof Item) registerItem((Item) obj);
                else if (obj instanceof Item[]) for (Item item : (Item[]) obj) registerItem(item);
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void registerItem(Item item)
    {
        String name = item.getUnlocalizedName();
        String[] strings = name.split("\\.");
        GameRegistry.registerItem(item, strings[strings.length - 1]);
    }
}