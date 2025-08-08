package com.example.custommotd;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(id = "custommotd", name = "CustomMOTD", version = "1.0.0", authors = {"YourName"})
public class CustomMOTDPlugin {

    private final ProxyServer server;
    private Favicon mainIcon;
    private Favicon pvpIcon;

    @Inject
    public CustomMOTDPlugin(ProxyServer server) {
        this.server = server;
        loadIcons();
    }

    private void loadIcons() {
        try {
            Path pluginDir = Path.of("plugins", "CustomMOTD");
            Files.createDirectories(pluginDir);
            Path mainIconPath = pluginDir.resolve("main.png");
            Path pvpIconPath = pluginDir.resolve("pvp.png");

            if (Files.exists(mainIconPath)) {
                mainIcon = Favicon.create(Files.readAllBytes(mainIconPath));
            }
            if (Files.exists(pvpIconPath)) {
                pvpIcon = Favicon.create(Files.readAllBytes(pvpIconPath));
            }
        } catch (IOException e) {
            server.getConsoleCommandSource().sendMessage("§c[CustomMOTD] Failed to load icons: " + e.getMessage());
        }
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        Optional<InetSocketAddress> virtualHostOpt = event.getConnection().getVirtualHost();
        if (virtualHostOpt.isEmpty()) return;

        String host = virtualHostOpt.get().getHostString().toLowerCase();

        String motd;
        Favicon icon;

        if (host.contains("pvp.example.com")) {
            motd = "§cWelcome to the §ePvP §cServer!";
            icon = pvpIcon != null ? pvpIcon : null;
        } else {
            motd = "§aWelcome to the §bMain §aServer!";
            icon = mainIcon != null ? mainIcon : null;
        }

        ServerPing original = event.getPing();
        ServerPing newPing = new ServerPing(
                original.getVersion(),
                original.getPlayers(),
                new ServerPing.Description(motd),
                icon
        );

        event.setPing(newPing);
    }
}
