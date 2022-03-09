package me.EtienneDx.RealEstate.ClaimAPI.GriefDefender;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.User;
import com.griefdefender.api.claim.TrustTypes;
import com.griefdefender.api.event.ChangeClaimEvent;
import com.griefdefender.api.event.Event;
import com.griefdefender.api.event.ProcessTrustUserEvent;
import com.griefdefender.api.event.RemoveClaimEvent;
import com.griefdefender.lib.kyori.adventure.text.Component;
import com.griefdefender.lib.kyori.event.EventBus;
import com.griefdefender.lib.kyori.event.EventSubscriber;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import me.EtienneDx.RealEstate.ClaimEvents;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;

public class GDPermissionListener
{

    public GDPermissionListener() {
        new ProcessTrustUserEventListener();
        new ChangeClaimEventListener();
        new RemoveClaimEventListener();
    }

    private class ProcessTrustUserEventListener {

        public ProcessTrustUserEventListener() {
            final EventBus<Event> eventBus = GriefDefender.getEventManager().getBus();

            eventBus.subscribe(ProcessTrustUserEvent.class, new EventSubscriber<ProcessTrustUserEvent>() {

                @Override
                public void on(@NonNull ProcessTrustUserEvent event) throws Throwable {
                    final User user = event.getUser();
                    if (user == null) {
                        return;
                    }
                    final Player player = Bukkit.getPlayer(user.getUniqueId());
                    if (player == null) {
                        return;
                    }
                    ClaimPermission permission = null;
                    if(event.getTrustType().equals(TrustTypes.ACCESSOR)) {
                        permission = ClaimPermission.ACCESS;
                    }
                    else if(event.getTrustType().equals(TrustTypes.BUILDER)) {
                        permission = ClaimPermission.BUILD;
                    }
                    else if(event.getTrustType().equals(TrustTypes.CONTAINER)) {
                        permission = ClaimPermission.CONTAINER;
                    }
                    else if(event.getTrustType().equals(TrustTypes.MANAGER)) {
                        permission = ClaimPermission.MANAGE;
                    }
                    String denialReason = ClaimEvents.onClaimPermission(
                        new GDClaim(event.getClaim()),
                        player,
                        permission
                    );
                    if (denialReason != null) {
                        event.setMessage(Component.text(denialReason));
                        event.cancelled(true);
                    }
                }
            });
        }
    }

    private class ChangeClaimEventListener {

        public ChangeClaimEventListener() {
            final EventBus<Event> eventBus = GriefDefender.getEventManager().getBus();

            eventBus.subscribe(ChangeClaimEvent.class, new EventSubscriber<ChangeClaimEvent>() {

                @Override
                public void on(@NonNull ChangeClaimEvent event) throws Throwable {
                    final User user = event.getCause().first(User.class).orElse(null);
                    if (user == null) {
                        return;
                    }
                    final Player player = Bukkit.getPlayer(user.getUniqueId());
                    if (player == null) {
                        return;
                    }
                    String denialReason = ClaimEvents.onClaimPermission(
                        new GDClaim(event.getClaim()),
                        player,
                        ClaimPermission.EDIT
                    );
                    if (denialReason != null) {
                        event.setMessage(Component.text(denialReason));
                        event.cancelled(true);
                    }
                }
            });
        }
    }

    private class RemoveClaimEventListener {

        public RemoveClaimEventListener() {
            final EventBus<Event> eventBus = GriefDefender.getEventManager().getBus();

            eventBus.subscribe(RemoveClaimEvent.class, new EventSubscriber<RemoveClaimEvent>() {

                @Override
                public void on(@NonNull RemoveClaimEvent event) throws Throwable {
                    final User user = event.getCause().first(User.class).orElse(null);
                    if (user == null) {
                        return;
                    }
                    final Player player = Bukkit.getPlayer(user.getUniqueId());
                    if (player == null) {
                        return;
                    }
                    String denialReason = ClaimEvents.onClaimPermission(
                        new GDClaim(event.getClaim()),
                        player,
                        ClaimPermission.EDIT
                    );
                    if (denialReason != null) {
                        event.setMessage(Component.text(denialReason));
                        event.cancelled(true);
                    }
                }
            });
        }
    }
}