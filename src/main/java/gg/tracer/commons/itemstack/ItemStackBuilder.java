package gg.tracer.commons.itemstack;

import com.google.common.collect.Lists;
import gg.tracer.commons.resource.ResourceSection;
import gg.tracer.commons.util.Messages;
import gg.tracer.commons.util.Pair;
import gg.tracer.commons.util.Reflection;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * @author Bradley Steele
 */
public class ItemStackBuilder implements Cloneable {

    // stack
    public Material material;
    public int amount = 1;
    public final Map<Enchantment, Integer> enchantments = new HashMap<>();

    // meta
    public String name;
    public List<String> lore = new ArrayList<>();
    public boolean unbreakable = false;
    public Integer customModelData = null;
    public final Set<ItemFlag> flags = new HashSet<>();
    public int damage;
    public final Map<NamespacedKey, Pair<PersistentDataType<Object, Object>, Object>> pdcs = new HashMap<>();

    protected ItemStackBuilder(Material material) {
        withMaterial(material);
    }

    protected ItemStackBuilder(ResourceSection section) {
        withMaterial(ItemStacks.matchMaterial(section.getString("material")));
        withAmount(section.getInt("amount", 1));

        if (section.contains("enchantments", false)) {
            ResourceSection es = section.getSection("enchantments");

            for (String key : es.getKeys()) {
                Enchantment enchantment = ItemStacks.matchEnchantment(key);

                if (enchantment != null) {
                    enchantments.put(enchantment, es.getInt(key));
                }
            }
        }

        withNameColored(section.getString("name"));
        withLoreColored(section.getList("lore", String.class));
        withUnbreakable(section.getBoolean("unbreakable", false));
        withCustomModelData(section.get("custom-model-data", Integer.class, null));

        for (String str : section.getList("flags", String.class)) {
            ItemFlag flag = Reflection.matchEnum(ItemFlag.class, str.toUpperCase());

            if (flag != null) {
                flags.add(flag);
            }
        }

        withDamage(section.getInt("damage", 0));
    }

    public ItemStackBuilder(ItemStackBuilder builder) {
        withMaterial(builder.material);
        withAmount(builder.amount);

        for (var enchant : builder.enchantments.entrySet()) {
            withEnchantment(enchant.getKey(), enchant.getValue());
        }

        withName(builder.name);
        withLore(new ArrayList<>(builder.lore));
        withUnbreakable(builder.unbreakable);
        withCustomModelData(builder.customModelData);
        withFlags(new HashSet<>(builder.flags).toArray(new ItemFlag[0]));
        withDamage(builder.damage);

        for (var kv : builder.pdcs.entrySet()) {
            var pdc = kv.getValue();
            withPersistentData(kv.getKey(), pdc.first, pdc.second);
        }
    }

    @Override
    public ItemStackBuilder clone() {
        return new ItemStackBuilder(this);
    }

    public ItemStack build() {
        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.setUnbreakable(unbreakable);
            meta.setCustomModelData(customModelData);

            meta.addItemFlags(flags.toArray(new ItemFlag[0]));

            if (meta instanceof Damageable damageable) {
                damageable.setDamage(damage);
            }

            PersistentDataContainer pdc = meta.getPersistentDataContainer();

            for (var k : pdcs.entrySet()) {
                var v = k.getValue();
                pdc.set(k.getKey(), v.first, v.second);
            }

            stack.setItemMeta(meta);
        }

        stack.addUnsafeEnchantments(enchantments);

        return stack;
    }

    // stack

    public ItemStackBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder withAmount(int amount) {
        amount = Math.min(amount, 1);

        this.amount = amount;
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }

    // meta

    public ItemStackBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ItemStackBuilder withNameColored(String name) {
        this.name = Messages.color(name);
        return this;
    }

    public ItemStackBuilder withLore(Iterable<? extends String> lore) {
        this.lore = Lists.newArrayList(lore);
        return this;
    }

    public ItemStackBuilder withLoreColored(Iterable<? extends String> lore) {
        this.lore = Messages.color(lore);
        return this;
    }

    public ItemStackBuilder withUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemStackBuilder withCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public ItemStackBuilder withFlags(ItemFlag... flags) {
        Collections.addAll(this.flags, flags);
        return this;
    }

    public ItemStackBuilder withDamage(int damage) {
        this.damage = damage;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T, Z> ItemStackBuilder withPersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        pdcs.put(key, new Pair<>((PersistentDataType<Object, Object>) type, value));
        return this;
    }

    public <T, Z> ItemStackBuilder withPersistentData(String key, Plugin plugin, PersistentDataType<T, Z> type, Z value) {
        return withPersistentData(NamespacedKey.fromString(key, plugin), type, value);
    }

    public <T, Z> ItemStackBuilder withPersistentData(String key, PersistentDataType<T, Z> type, Z value) {
        return withPersistentData(key, null, type, value);
    }
}
