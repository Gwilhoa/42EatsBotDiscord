package fr.eats.commands.command;

import fr.eats.commands.builder.Command;
import fr.eats.commands.objects.Documents;
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

	@Command(name = "addmeal", description = "ajouter un plat", type = Command.ExecutorType.USER)
	private void addmeal(Message msg) {
		if (!isBartender(msg.getMember()))
			return;
		String[] args = msg.getContentRaw().split(" ");
		String name;
		Double price;
		Double adherence;
		if (args.length == 5) {
			name = args[1];
			try {
				price = Double.parseDouble(args[2]);
				adherence = Double.parseDouble(args[3]);
				if (doc.addMeals(name, price, adherence, args[4]) < 0)
					msg.getChannel().sendMessage("plat déjà définis").queue();
				else
					msg.getChannel().sendMessage("le plat " + name + " est au prix de " + price+ "€ et pour les adhrents "+ adherence + "€").queue();
			} catch (NumberFormatException e) {
				msg.getChannel().sendMessage("mauvais format : les prix sont mal définis \n>addmeal NOM PRIX PRIX_ADHERENCE AVEC_INGREDIENT(1 si oui sinon non)").queue();
			}
		}
		else
			msg.getChannel().sendMessage("mauvais format : trop ou pas assez d'argument \n>addmeal NOM PRIX PRIX_ADHERENCE").queue();
	}

	@Command(name = "addsnack", description = "ajouter un dessert, un snack", type = Command.ExecutorType.USER)
	private void addsnack(Message msg) {
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
				if (!doc.addSnack(name, price, adherence))
					msg.getChannel().sendMessage("snack déjà définis").queue();
				else
					msg.getChannel().sendMessage("le snack/ dessert " + name + " est au prix de " + price+ "€ et pour les adhrents "+ adherence + "€").queue();
			} catch (NumberFormatException e) {
				msg.getChannel().sendMessage("mauvais format : les prix sont mal définis \n>addsnack NOM PRIX PRIX_ADHERENCE").queue();
			}
		}
		else
			msg.getChannel().sendMessage("mauvais format : trop ou pas assez d'argument \n>addsnack NOM PRIX PRIX_ADHERENCE").queue();
	}


	@Command(name = "addsauce", description = "ajouter une sauce", type = Command.ExecutorType.USER)
	private void addsauces(Message msg)
	{
		if(!isBartender(msg.getMember()))
			return;
		String[] args = msg.getContentRaw().split(" ");
		if (args.length == 2) {
			if (!doc.addSauces(args[1]))
				msg.getChannel().sendMessage("déjà ajouté").queue();
			else
				msg.getChannel().sendMessage("sauce ajoutés avec succès").queue();
		}
		else
			msg.getChannel().sendMessage(">addsauces NOM").queue();
	}

	@Command(name = "addingredient", description = "ajouter des ingrédients", type = Command.ExecutorType.USER)
	private void addingredients(Message msg)
	{
		if(!isBartender(msg.getMember()))
			return;
		String[] args = msg.getContentRaw().split(" ");
		if (args.length == 2) {
			if (!doc.addIngredients(args[1]))
				msg.getChannel().sendMessage("déjà ajouté").queue();
			else
				msg.getChannel().sendMessage("ingrédient ajoutés avec succès").queue();
		}
		else
			msg.getChannel().sendMessage(">addingredients NOM").queue();
	}
}
