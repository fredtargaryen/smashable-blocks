// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks;

import com.fredtargaryen.smashableblocks.attachment.DefaultSmasherImpl;
import com.fredtargaryen.smashableblocks.attachment.Smasher;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DataReference.MODID);

    public static final Supplier<AttachmentType<Smasher>> SMASHER = ATTACHMENT_TYPES.register(
            "smasher", () -> AttachmentType.<Smasher>builder(() -> new DefaultSmasherImpl(null)).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
