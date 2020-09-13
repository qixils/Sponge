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
package org.spongepowered.common.service.server.ban;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.ban.Ban;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;

/**
 * Redirects all calls to the {@link BanService}.
 */
public class SpongeUserListBans extends BanList {

    public SpongeUserListBans(final File bansFile) {
        super(bansFile);
    }

    @Override
    protected boolean hasEntry(final com.mojang.authlib.GameProfile entry) {
        return Sponge.getServer().getServiceProvider().banService().isBanned((GameProfile) entry);
    }

    @Override
    public ProfileBanEntry getEntry(final com.mojang.authlib.GameProfile obj) {
        return (ProfileBanEntry) Sponge.getServer().getServiceProvider().banService().getBanFor((GameProfile) obj).orElse(null);
    }

    @Override
    public String[] getKeys() {
        final List<String> names = new ArrayList<>();
        for (final Ban.Profile ban : Sponge.getServer().getServiceProvider().banService().getProfileBans()) {
            ban.getProfile().getName().ifPresent(names::add);
        }
        return names.toArray(new String[names.size()]);
    }

    @Override
    public void addEntry(final ProfileBanEntry entry) {
        Sponge.getServer().getServiceProvider().banService().addBan((Ban) entry);
    }

    @Override
    public boolean isEmpty() {
        return Sponge.getServer().getServiceProvider().banService().getProfileBans().isEmpty();
    }

    @Override
    public void removeEntry(final com.mojang.authlib.GameProfile entry) {
        Sponge.getServer().getServiceProvider().banService().pardon((GameProfile) entry);
    }

    @Nullable
    public com.mojang.authlib.GameProfile getBannedProfile(final String username) {
        for (final Ban.Profile ban : Sponge.getServer().getServiceProvider().banService().getProfileBans()) {
            if (ban.getProfile().getName().isPresent() && ban.getProfile().getName().get().equals(username)) {
                return (com.mojang.authlib.GameProfile) ban.getProfile();
            }
        }

        return null;
    }

}