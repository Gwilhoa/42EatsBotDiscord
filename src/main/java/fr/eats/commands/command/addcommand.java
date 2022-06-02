package fr.eats.commands.command;

import fr.eats.commands.builder.Command;
import net.dv8tion.jda.api.entities.Message;

import static fr.eats.commands.objects.Documents.doc;
import static fr.eats.commands.objects.Documents.isBartender;

public class addcommand {
	@Command(name = "addboisson", description = "ajouter une boisson", type = Command.ExecutorType.USER)
	private void addboisson(Message msg) {
		if (!isBartender(msg.getMember()))
			return;
		String[] args = msg.getContentRaw().split(" ");
		String name;
		Double price;
		Double adherence;
		if (args.length == 4) {
			name = args[1];
			try {
				price = Double.parseDouble(args[2]);
				adherence = Double.parseDouble(args[3]);
				if (doc.addBoisson(name, price, adherence) < 0)
					msg.getChannel().sendMessage("boisson déjà définis").queue();
				else
					msg.getChannel().sendMessage("la boisson " + name + " est au prix de " + price+ "€ et pour les adhrents "+ adherence + "€").queue();
			} catch (NumberFormatException e) {
				msg.getChannel().sendMessage("mauvais format : les prix sont mal définis \n>addboisson NOM PRIX PRIX_ADHERENCE").queue();
			}
		}
		else
			msg.getChannel().sendMessage("mauvais format : trop ou pas assez d'argument \n>addboisson NOM PRIX PRIX_ADHERENCE").queue();
	}

	@Command(name = "addmeal", description = "ajouter une boisson", type = Command.ExecutorType.USER)
	private void addmeal(Message msg) {
		if (!isBartender(msg.getMember()))
			return;
		String[] args = msg.getContentRaw().split(" ");
		String name;
		Double price;
		Double adherence;
		if (args.length == 4) {
			name = args[1];
			try {
				price = Double.parseDouble(args[2]);
				adherence = Double.parseDouble(args[3]);
				if (doc.addMeals(name, price, adherence) < 0)
					msg.getChannel().sendMessage("plat déjà définis").queue();
				else
					msg.getChannel().sendMessage("le plat " + name + " est au prix de " + price+ "€ et pour les adhrents "+ adherence + "€").queue();
			} catch (NumberFormatException e) {
				msg.getChannel().sendMessage("mauvais format : les prix sont mal définis \n>addboisson NOM PRIX PRIX_ADHERENCE").queue();
			}
		}
		else
			msg.getChannel().sendMessage("mauvais format : trop ou pas assez d'argument \n>addboisson NOM PRIX PRIX_ADHERENCE").queue();
	}
}
