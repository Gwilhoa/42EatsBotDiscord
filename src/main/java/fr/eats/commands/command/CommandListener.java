

package fr.eats.commands.command;



import fr.eats.commands.BotDiscord;
import fr.eats.commands.builder.Command;
import fr.eats.commands.builder.CommandMap;
import fr.eats.commands.objects.activity;
import fr.eats.commands.objects.Documents;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;



import java.io.IOException;


import static fr.eats.commands.event.BotListener.isopen;
import static fr.eats.commands.objects.Documents.doc;
import static fr.eats.commands.objects.Documents.isBartender;


/**
 * fichier de commandes de base
 */
public class CommandListener {

	private final BotDiscord botDiscord;
	/**
	 * intialisation de l'objet
	 *
	 * @param botDiscord
	 * @param commandMap
	 */
	public CommandListener(BotDiscord botDiscord, CommandMap commandMap) {
		this.botDiscord = botDiscord;
	}

	/**
	 * arreter le bot depuis la console NORMALEMENT
	 *
	 * @param jda le bot a arreter
	 */
	@Command(name = "-", type = Command.ExecutorType.CONSOLE)
	private void stop(JDA jda) {
		jda.getPresence().setStatus(OnlineStatus.OFFLINE);
		botDiscord.setRunning(false);
	}


	@Command(name = "open", description = "annoncer l'ouverture du foyer", type = Command.ExecutorType.USER)
	private void open(Message msg, JDA jda) throws IOException {
		if (!msg.getMember().getId().equals("315431392789921793") && !isBartender(msg.getMember()))
			return;
		if (isopen) {
			 msg.getChannel().sendMessage("le foyer est déjà ouvert").queue();
			 return;
		}
		if (doc.getChannelAnnounceId() == null)
			msg.getChannel().sendMessage("@everyone le foyer ouvre ! :)").queue();
		else
			botDiscord.getJda().getGuildById(doc.getServId()).getTextChannelById(doc.getChannelAnnounceId()).sendMessage("@everyone le foyer ouvre ! :)").queue();
		activity act = new activity("le foyer est ouvert !", null, Activity.ActivityType.WATCHING);
		jda.getPresence().setPresence(OnlineStatus.ONLINE, act);
		isopen = !isopen;
	}

	@Command(name = "close", description = "annoncer l'ouverture du foyer", type = Command.ExecutorType.USER)
	private void close(Message msg, JDA jda) throws IOException {
		if (!msg.getMember().getId().equals("315431392789921793") && !isBartender(msg.getMember()))
			return;
		if (!isopen) {
			msg.getChannel().sendMessage("le foyer est déjà fermé").queue();
			return;
		}
		if (doc.getChannelAnnounceId() == null)
			msg.getChannel().sendMessage("@everyone le foyer ferme ! :(").queue();
		else
			botDiscord.getJda().getGuildById(doc.getServId()).getTextChannelById(doc.getChannelAnnounceId()).sendMessage("@everyone le foyer ferme ! :(").queue();
		activity act = new activity("le foyer est fermé !", null, Activity.ActivityType.WATCHING);
		jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, act);
		isopen = !isopen;
	}

	@Command(name = "setannouncechannel", description = "changer le channel d'annonce", type = Command.ExecutorType.USER)
	private void setAnnounceChannel(Message msg)
	{
		if (!msg.getMember().getId().equals("315431392789921793") && !isBartender(msg.getMember()))
			return;
		if (msg.getMentions().getChannels().isEmpty())
			msg.getChannel().sendMessage("aucun channel mentionné detecté").queue();
		else {
			msg.getChannel().sendMessage("le nouveau channel d'annonce est " + msg.getMentions().getChannels().get(0).getAsMention()).queue();
			doc.setChannelAnnounceId(msg.getMentions().getChannels().get(0).getId());
		}
	}

	@Command(name = "setcommandschannel", description = "changer le channel d'annonce", type = Command.ExecutorType.USER)
	private void setCommandsChannel(Message msg)
	{
		if (!msg.getMember().getId().equals("315431392789921793") && !isBartender(msg.getMember()))
			return;
		if (msg.getMentions().getChannels().isEmpty())
			msg.getChannel().sendMessage("aucun channel mentionné detecté").queue();
		else {
			msg.getChannel().sendMessage("le nouveau channel de commande est " + msg.getMentions().getChannels().get(0).getAsMention()).queue();
			doc.setCommandsChannelId(msg.getMentions().getChannels().get(0).getId());
		}
	}
}