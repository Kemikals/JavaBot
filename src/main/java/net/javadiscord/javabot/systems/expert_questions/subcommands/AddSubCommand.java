package net.javadiscord.javabot.systems.expert_questions.subcommands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.javadiscord.javabot.command.Responses;
import net.javadiscord.javabot.systems.expert_questions.ExpertSubcommand;
import net.javadiscord.javabot.systems.expert_questions.dao.ExpertQuestionRepository;
import net.javadiscord.javabot.systems.expert_questions.model.ExpertQuestion;

import java.sql.Connection;

public class AddSubCommand extends ExpertSubcommand {
	@Override
	protected ReplyAction handleCommand(SlashCommandEvent event, Connection con) throws Exception {
		var textOption = event.getOption("text");
		if (textOption == null) {
			return Responses.error(event, "Missing required Arguments");
		}
		var text = textOption.getAsString();
		ExpertQuestion question = new ExpertQuestion();
		question.setGuildId(event.getGuild().getIdLong());
		question.setText(text);
		var repo = new ExpertQuestionRepository(con);
		repo.save(question);
		return Responses.success(event, "Inserted Expert Question",
				String.format("Successfully inserted new Expert Question:\n\n```%s\n```", text));
	}
}
