package fr.communaywen.core.commands.contest;

import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.contest.cache.ContestCache;
import fr.communaywen.core.contest.managers.ContestManager;
import fr.communaywen.core.contest.menu.ContributionMenu;
import fr.communaywen.core.contest.menu.VoteMenu;
import fr.communaywen.core.credit.Credit;
import fr.communaywen.core.credit.Feature;
import fr.communaywen.core.utils.constant.MessageManager;
import fr.communaywen.core.utils.constant.MessageType;
import fr.communaywen.core.utils.constant.Prefix;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Feature("Contest")
@Credit("iambibi_")
@Command("contest")
@Description("Ouvre l'interface des festivals, et quand un festival commence, vous pouvez choisir votre camp")
public class ContestCommand {
    private final AywenCraftPlugin plugin;
    private final FileConfiguration eventConfig;
    private final ContestManager contestManager;
    private final ContestCache contestCache;

    public ContestCommand(AywenCraftPlugin plugins, FileConfiguration eventConfigs, ContestManager manager) {
        this.contestManager = manager;
        this.contestCache = plugins.getManagers().getContestCache();
        plugin = plugins;
        eventConfig = eventConfigs;
    }

    @Cooldown(4)
    @DefaultFor("~")
    public void defaultCommand(Player player) {
        int phase = contestCache.getPhaseCache();
        int camp = contestCache.getPlayerCampsCache(player);
        if (phase==2) {
            VoteMenu menu = new VoteMenu(player, plugin, contestManager);
            menu.open();
        } else if (phase==3 && camp <= 0) {
            VoteMenu menu = new VoteMenu(player, plugin, contestManager);
            menu.open();
        } else if (phase==3) {
            ContributionMenu menu = new ContributionMenu(player, plugin, contestManager);
            menu.open();

        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.FRENCH);
            DayOfWeek dayStartContestOfWeek = DayOfWeek.from(formatter.parse(contestCache.getStartDateCache()));

            int days = (dayStartContestOfWeek.getValue() - contestManager.getCurrentDayOfWeek().getValue() + 7) % 7;

            MessageManager.sendMessageType(player, "§cIl n'y a aucun Contest ! Revenez dans " + days + " jour(s).", Prefix.CONTEST, MessageType.ERROR, true);
        }
    }

    @Subcommand("setphase")
    @Description("Permet de lancer une procédure de phase")
    @CommandPermission("ayw.command.contest.setphase")
    public void setphase(Integer phase) {
        if (phase == 1) {
            contestManager.initPhase1();
        } else if (phase == 2) {
            contestManager.initPhase2(plugin, eventConfig);
        } else if (phase == 3) {
            contestManager.initPhase3(plugin, eventConfig);
        }
    }

    @Subcommand("setcontest")
    @Description("Permet de définir un Contest")
    @CommandPermission("ayw.command.contest.setcontest")
    @AutoComplete("@colorContest")
    public void setcontest(Player player, String camp1, @Named("colorContest") String color1, String camp2, @Named("colorContest") String color2) {
        int phase = contestCache.getPhaseCache();
        if (phase == 1) {
            if (contestManager.getColorContestList().contains(color1) || contestManager.getColorContestList().contains(color2)) {
                contestManager.deleteTableContest("contest");
                contestManager.deleteTableContest("camps");
                contestManager.insertCustomContest(camp1, color1, camp2, color2);

                MessageManager.sendMessageType(player, "§aLe Contest : " + camp1 + " VS " + camp2 + " a bien été sauvegarder\nMerci d'attendre que les données en cache s'actualise.", Prefix.STAFF, MessageType.SUCCESS, true);
            } else {
                MessageManager.sendMessageType(player, "§c/contest setcontest <camp1> <color1> <camp2> <color2> et color doit comporter une couleur valide", Prefix.STAFF, MessageType.ERROR, true);
            }
        } else {
            MessageManager.sendMessageType(player, "§cVous pouvez pas définir un contest lorsqu'il a commencé", Prefix.STAFF, MessageType.ERROR, true);
        }
    }

    @Subcommand("settrade")
    @Description("Permet de définir un Trade")
    @CommandPermission("ayw.command.contest.settrade")
    @AutoComplete("@trade")
    public void settrade(Player player, @Named("trade") String trade, int amount, int amount_shell) {
        FileConfiguration config = plugin.getConfig();
        List<Map<?, ?>> trades = config.getMapList("contest.contestTrades");

        boolean tradeFound = false;

        for (Map<?, ?> tradeEntry : trades) {
            if (tradeEntry.get("ress").equals(trade)) {
                ((Map<String, Object>) tradeEntry).put("amount", amount);
                ((Map<String, Object>) tradeEntry).put("amount_shell", amount_shell);
                tradeFound = true;
                break;
            }
        }

        if (tradeFound) {
            plugin.saveConfig();
            MessageManager.sendMessageType(player, "Le trade de " + trade + " a été mis à jour avec " + amount + " pour " + amount_shell + " coquillages de contest.", Prefix.STAFF, MessageType.SUCCESS, true);
        } else {
            MessageManager.sendMessageType(player, "Le trade n'existe pas.\n/contest settrade <mat> <amount> <amount_shell>", Prefix.STAFF, MessageType.ERROR, true);
        }
    }

    @Subcommand("addpoints")
    @Description("Permet d'ajouter des points a un membre")
    @CommandPermission("ayw.command.contest.addpoints")
    public void addpoints(Player player, Player target, Integer points) {
        contestManager.addPointPlayer(points + contestManager.getPlayerPoints(target), target);

        MessageManager.sendMessageType(player, "§aVous avez ajouté " + points + " §apoint(s) à " + target.getName(), Prefix.STAFF, MessageType.SUCCESS, true);
    }

}
