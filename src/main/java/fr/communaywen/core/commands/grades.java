package fr.communaywen.core.commands;

import fr.communaywen.core.credit.Credit;
import fr.communaywen.core.credit.Feature;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Range;

@Feature("grades")
@Credit("llex_tv")
public class Grades {
    @Command({"grades"})
    @Description("Achete des Grades")
    public void onCommand(Player player) {
        player.sendMessage("Dev en cours");
      
    }
}
