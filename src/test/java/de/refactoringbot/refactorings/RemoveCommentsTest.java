package de.refactoringbot.refactorings;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.refactoringbot.model.botissue.BotIssue;
import de.refactoringbot.model.configuration.GitConfiguration;
import de.refactoringbot.model.exceptions.BotRefactoringException;
import de.refactoringbot.refactoring.supportedrefactorings.RemoveCommentedOutCode;
import de.refactoringbot.resources.removecomments.TestDataClassRemoveComments;

public class RemoveCommentsTest extends AbstractRefactoringTests {

	private static final Logger logger = LoggerFactory.getLogger(RemoveCommentsTest.class);

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testRemoveLineComments() throws Exception {
		// This should also remove the line directly below, but not the next one
		int lineWithCommentToBeRemoved = 6;
		File modifiedTempFile = removeComment(lineWithCommentToBeRemoved);
		
		String lineContent = getStrippedContentFromFile(modifiedTempFile, lineWithCommentToBeRemoved);
		assertThat(lineContent).isEqualTo("// Normal comment - This one shouldn't be removed");
	}

	@Test
	public void testRemoveBlockComment() throws Exception {
		int lineWithCommentToBeRemoved = 10;
		File modifiedTempFile = removeComment(lineWithCommentToBeRemoved);
		
		String lineContent = getStrippedContentFromFile(modifiedTempFile, lineWithCommentToBeRemoved);
		assertThat(lineContent).isEqualTo("return c;");
	}
	
	@Test
	public void testRemoveNotExistingComment() throws Exception {
		exception.expect(BotRefactoringException.class);
		
		int lineWithCommentToBeRemoved = 3;
		removeComment(lineWithCommentToBeRemoved);
	}

	/**
	 * Performs the refactoring
	 * 
	 * @param line
	 * @return modified temporary file of test class
	 * @throws Exception
	 */
	private File removeComment(int line) throws Exception {
		// arrange
		File tempFile = createTempCopyOfTestResourcesFile(TestDataClassRemoveComments.class);
		BotIssue issue = new BotIssue();
		GitConfiguration gitConfig = new GitConfiguration();
		RemoveCommentedOutCode refactoring = new RemoveCommentedOutCode();

		gitConfig.setRepoFolder("");
		issue.setFilePath(tempFile.getAbsolutePath());
		issue.setLine(line);

		// act
		String outputMessage = refactoring.performRefactoring(issue, gitConfig);
		logger.info(outputMessage);
		
		return tempFile;
	}

}