

package fr.eats.commands.command;



import fr.eats.commands.BotDiscord;
import fr.eats.commands.builder.Command;
import fr.eats.commands.builder.CommandMap;
import fr.eats.commands.event.BotListener;
import fr.eats.commands.objects.activity;
import fr.eats.commands.objects.Documents;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;


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
		if (!isBartender(msg.getMember()))
			return;
		if (isopen) {
			 msg.getChannel().sendMessage("le foyer est déjà ouvert").queue();
			 return;
		}
		else if (!doc.getServId().equals(msg.getGuild().getId())) {
			msg.getChannel().sendMessage("vous n'etes pas sur le bon serveur ou le serveur est mal défini").queue();
			return;
		}
		else if (doc.getChannelAnnounceId() == null || msg.getGuild().getTextChannelById(doc.getChannelAnnounceId()) == null)
		{
			msg.getChannel().sendMessage("le salon d'annonce n'est pas définis").queue();
			return;
		}
		else if (doc.getCommandsChannelId() == null || msg.getGuild().getTextChannelById(doc.getCommandsChannelId()) == null){
			msg.getChannel().sendMessage("le salon des commandes n'est pas définis").queue();
			return;
		}
		botDiscord.getJda().getGuildById(doc.getServId()).getTextChannelById(doc.getChannelAnnounceId()).sendMessage("le foyer ouvre ! :)\ntu peux executer /command").queue();
		activity act = new activity("le foyer est ouvert !", null, Activity.ActivityType.WATCHING);
		jda.getPresence().setPresence(OnlineStatus.ONLINE, act);
		isopen = !isopen;
	}

	@Command(name = "close", description = "annoncer la fermeture du foyer", type = Command.ExecutorType.USER)
	private void close(Message msg, JDA jda) throws IOException {
		if (!isBartender(msg.getMember()))
			return;
		if (!isopen) {
			msg.getChannel().sendMessage("le foyer est déjà fermé").queue();
			return;
		}
		if (doc.getChannelAnnounceId() == null) {
			msg.getChannel().sendMessage("le foyer ferme ! :(").queue();
		}
		else
			botDiscord.getJda().getGuildById(doc.getServId()).getTextChannelById(doc.getChannelAnnounceId()).sendMessage("le foyer ferme ! :(").queue();
		activity act = new activity("le foyer est fermé !", null, Activity.ActivityType.WATCHING);
		jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, act);
		isopen = !isopen;
		BotListener.timeclose = System.currentTimeMillis();
	}

	@Command(name = "setannouncechannel", description = "changer le channel d'annonce", type = Command.ExecutorType.USER)
	private void setAnnounceChannel(Message msg)
	{
		if (!isBartender(msg.getMember()))
			return;
		if (msg.getMentions().getChannels().isEmpty())
			msg.getChannel().sendMessage("aucun channel mentionné detecté").queue();
		else {
			msg.getChannel().sendMessage("le nouveau channel d'annonce est " + msg.getMentions().getChannels().get(0).getAsMention()).queue();
			doc.setChannelAnnounceId(msg.getMentions().getChannels().get(0).getId());
			doc.setServId(msg.getGuild().getId());
		}
	}

	@Command(name = "setcommandschannel", description = "changer le channel d'annonce", type = Command.ExecutorType.USER)
	private void setCommandsChannel(Message msg)
	{
		if (!isBartender(msg.getMember()))
			return;
		if (msg.getMentions().getChannels().isEmpty())
			msg.getChannel().sendMessage("aucun channel mentionné detecté").queue();
		else {
			msg.getChannel().sendMessage("le nouveau channel de commande est " + msg.getMentions().getChannels().get(0).getAsMention()).queue();
			doc.setCommandsChannelId(msg.getMentions().getChannels().get(0).getId());
			doc.setServId(msg.getGuild().getId());
		}
	}

	@Command(name = "command", description = "passer commande", type = Command.ExecutorType.USER)
	private void command(Message msg) {
		command(msg.getMember(), msg.getTextChannel(), null);
	}


	public static void command(Member m, TextChannel tc, SlashCommandInteraction event){
		if (!isopen)
		{
			if (event == null)
				tc.sendMessage("le foyer est fermé").queue();
			else
				event.reply("le foyer est fermé").queue();
			return;
		}
		if (event != null)
			event.reply("oui").complete().deleteOriginal().queue();
		PrivateChannel pc = m.getUser().openPrivateChannel().complete();
		EmbedBuilder eb = new EmbedBuilder().setTitle("passer commande").setColor(Color.ORANGE).setAuthor("bonjour " + m.getEffectiveName());
		eb.setDescription("es-tu adhérent ?");
		ArrayList<ActionRow> bttn = new ArrayList<>();
		bttn.add(ActionRow.of(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("adherence_oui", "oui")));
		bttn.add(ActionRow.of(Button.danger("adherence_non", "non")));
		pc.sendMessageEmbeds(eb.build()).setActionRows(bttn).queue();
	}
}