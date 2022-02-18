/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.server.dedicated;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.SpongeCommon;
import org.spongepowered.common.bridge.server.players.GameProfileCacheBridge;
import org.spongepowered.common.launch.Launch;
import org.spongepowered.common.mixin.core.server.MinecraftServerMixin;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin extends MinecraftServerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void impl$setServerOnGame(final Thread $$0, final LevelStorageSource.LevelStorageAccess $$1, final PackRepository $$2,
        final WorldStem $$3, final DedicatedServerSettings $$4, final DataFixer $$5, final MinecraftSessionService $$6,
        final GameProfileRepository $$7, final GameProfileCache $$8, final ChunkProgressListenerFactory $$9, final CallbackInfo ci) {

        SpongeCommon.game().setServer(this);
        $$8.load();
    }

    @Override
    protected void loadLevel() {
        this.shadow$detectBundledResources();
        this.worldManager().loadLevel();
    }

    @Override
    public boolean bridge$performAutosaveChecks() {
        return this.shadow$isRunning();
    }

    @Redirect(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/GameProfileCache;save()V"))
    private void onSave(final GameProfileCache cache) {
        ((GameProfileCacheBridge) cache).bridge$setCanSave(true);
        cache.save();
        ((GameProfileCacheBridge) cache).bridge$setCanSave(false);
    }

    @Inject(method = "stopServer", at = @At("TAIL"))
    private void impl$callStoppedGame(final CallbackInfo ci) {
        Launch.instance().lifecycle().callStoppedGameEvent();
    }
}
