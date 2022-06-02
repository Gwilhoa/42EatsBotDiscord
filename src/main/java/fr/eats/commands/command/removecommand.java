package fr.eats.commands.command;

import fr.eats.commands.builder.Command;
import fr.eats.commands.objects.Documents;
import net.dv8tion.jda.api.entities.Message;

public class removecommand {
	@Command(name = "removeboisson", description = "supprimer la boisson du catalogue", type = Command.ExecutorType.USER)
	private void removeboisson(Message msg)
	{
		if (Documents.doc.removeBoisson(msg.getContentRaw().split(" ")[1]) < 0)
			msg.getChannel().sendMessage("la boisson "+ msg.getContentRaw().split(" ")[1] + "n'existe pas").queue();
		else
			msg.getChannel().sendMessage("boisson "+ msg.getContentRaw().split(" ")[1] +" a bien ete supprimé").queue();
	}
	@Command(name = "removemeal", description = "supprimer le plat du catalogue", type = Command.ExecutorType.USER)
	private void removemeal(Message msg)
	{
		if (Documents.doc.removeMeals(msg.getContentRaw().split(" ")[1]) < 0)
			msg.getChannel().sendMessage("le plat "+ msg.getContentRaw().split(" ")[1] + "n'existe pas").queue();
		else
			msg.getChannel().sendMessage("le plat "+ msg.getContentRaw().split(" ")[1] +" a bien ete supprimé").queue();
	}
}
