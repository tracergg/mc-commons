package gg.tracer.commons.itemstack;

import gg.tracer.commons.resource.ResourceSection;
import gg.tracer.commons.util.Numbers;
import gg.tracer.commons.util.Reflection;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bradley Steele
 */
public final class ItemStacks {

    private static final Map<NamespacedKey, Sound> SOUND_MAP = new HashMap<>();

    static {
        for (Sound sound : Sound.values()) {
            SOUND_MAP.put(sound.getKey(), sound);
        }
    }

    // builder

    public static ItemStackBuilder builder(Material material) {
        return new ItemStackBuilder(material);
    }

    public static ItemStackBuilder builder(ResourceSection section) {
        if (section == null) {
            return null;
        }

        return new ItemStackBuilder(section);
    }

    public static ItemStackBuilder builder() {
        return builder(Material.AIR);
    }


    // persistent data container

    public static <T, Z> Z getPDCValue(ItemStack stack, NamespacedKey key, PersistentDataType<T, Z> type, Z fallback) {
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            return meta.getPersistentDataContainer().getOrDefault(key, type, fallback);
        }

        return fallback;
    }

    public static <T, Z> Z getPDCValue(ItemStack stack, NamespacedKey key, PersistentDataType<T, Z> type) {
        return getPDCValue(stack, key, type, null);
    }

    public static <T, Z> Z getPDCValue(ItemStack stack, Plugin plugin, String key, PersistentDataType<T, Z> type) {
        return getPDCValue(stack, new NamespacedKey(plugin, key), type);
    }

    public static <T, Z> Z setPDCValue(ItemStack stack, NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(key, type, value);
            stack.setItemMeta(meta);
        }

        return value;
    }

    public static <T, Z> void setPDCValueIfMissing(ItemStack stack, NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta meta = stack.getItemMeta();

        if (meta != null && !hasPDC(stack, key, type)) {
            meta.getPersistentDataContainer().set(key, type, value);
            stack.setItemMeta(meta);
        }
    }

    public static <T extends Number, Z extends Number> Z incrementPDCValue(ItemStack stack, NamespacedKey key, PersistentDataType<T, Z> type, Z fallback, Z amount) {
        Z value = Numbers.add(getPDCValue(stack, key, type, fallback), amount);
        setPDCValue(stack, key, type, value);

        return value;
    }

    public static void removePDCKey(ItemStack stack, NamespacedKey key) {
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().remove(key);
            stack.setItemMeta(meta);
        }
    }

    public static <T, Z> boolean hasPDC(ItemStack stack, NamespacedKey key, PersistentDataType<T, Z> type) {
        ItemMeta meta = stack.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(key, type);
    }


    // match

    public static Material matchMaterial(String material, Material fallback) {
        if (material == null) {
            return fallback;
        }

        if (material.contains(":")) {
            NamespacedKey nsk = NamespacedKey.fromString(material);

            if (nsk != null) {
                material = nsk.getKey();
            }
        }

        return Reflection.matchEnum(Material.class, material.toUpperCase(), fallback);
    }

    public static Material matchMaterial(String material) {
        return matchMaterial(material, null);
    }

    public static Enchantment matchEnchantment(String enchantment, Enchantment fallback) {
        if (enchantment == null) {
            return fallback;
        }

        NamespacedKey nsk = NamespacedKey.fromString(enchantment);
        Enchantment enchant = Enchantment.getByKey(nsk);

        return enchant != null
                ? enchant
                : Reflection.getFieldValue(Enchantment.class, enchantment.toUpperCase(), null, fallback);
    }

    public static Enchantment matchEnchantment(String enchantment) {
        return matchEnchantment(enchantment, null);
    }

    public static Sound matchSound(String sound, Sound fallback) {
        if (sound == null) {
            return fallback;
        }

        NamespacedKey nsk = NamespacedKey.fromString(sound);
        Sound s = SOUND_MAP.get(nsk);

        return s != null
                ? s
                : Reflection.matchEnum(Sound.class, sound, fallback);
    }

    public static Sound matchSound(String sound) {
        return matchSound(sound, null);
    }


    // damage

    public static boolean isUnbreakable(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return false;
        }

        return stack.getItemMeta().isUnbreakable();
    }

    public static void damage(ItemStack stack, int damage, boolean ignoreUnbreakable) {
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }

        ItemMeta meta = stack.getItemMeta();

        if (meta.isUnbreakable() && !ignoreUnbreakable) {
            return;
        }

        if (meta instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() + damage);
            stack.setItemMeta(meta);
        }
    }

    public static void damage(ItemStack stack, int damage) {
        damage(stack, damage, false);
    }
}
