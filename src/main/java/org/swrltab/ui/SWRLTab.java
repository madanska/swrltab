package org.swrltab.ui;

import org.swrlapi.core.SWRLAPIFactory;
import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.drools.DroolsFactory;
import org.swrlapi.exceptions.SWRLAPIException;
import org.swrlapi.ui.controller.SWRLAPIApplicationController;
import org.swrlapi.ui.model.SWRLAPIApplicationModel;
import org.swrlapi.ui.view.SWRLAPIApplicationView;
import org.swrlapi.ui.view.rules.SWRLAPIRulesView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Standalone SWRLAPI-based application that presents a SWRL editor and rule execution graphical interface.
 * <p>
 * The Drools rule engine is used for rule execution.
 * <p>
 * To invoke from Maven put <code>org.swrltab.ui.SWRLTab</code> in the <code>mainClass</code> element of
 * the <code>exec-maven-plugin</code> plugin configuration in the Maven project POM and run with
 * the <code>exec:java</code> goal.
 *
 * @see SQWRLTab, SWRLAPIRulesView
 */
public class SWRLTab extends JFrame implements SWRLAPIApplicationView
{
	private static final long serialVersionUID = 1L;

	private static final String APPLICATION_NAME = "SWRLTabRules";
	private static final int APPLICATION_WIDTH = 1000;
	private static final int APPLICATION_HEIGHT = 580;

	private final SWRLAPIRulesView rulesView;

	public static void main(String[] args)
	{
		// TODO Hard code temporarily for testing. SWRLCoreTests, SQWRLCollectionsTests, SQWRLCoreTests, SWRLInferenceTests
		String owlFileName = SWRLTab.class.getClassLoader().getResource("projects/SQWRLCoreTests.owl").getFile();
		File owlFile = new File(owlFileName);

		try {
			// Create a SWRLAPI OWL ontology from the OWL ontology in the supplied file
			SWRLAPIOWLOntology swrlapiOWLOntology = SWRLAPIFactory.createOntology(owlFile);

			// Create a Drools-based rule engine
			SWRLRuleEngine ruleEngine = SWRLAPIFactory.createQueryEngine(swrlapiOWLOntology,
					DroolsFactory.getSWRLRuleEngineCreator());

			// Create the application model, supplying it with the ontology and rule engine
			SWRLAPIApplicationModel applicationModel = SWRLAPIFactory.createApplicationModel(swrlapiOWLOntology, ruleEngine);

			// Create the application controller
			SWRLAPIApplicationController applicationController = SWRLAPIFactory.createApplicationController(applicationModel);

			// Create the application view
			SWRLTab applicationView = new SWRLTab(applicationController);

			// Make the view visible
			applicationView.setVisible(true);
		} catch (SWRLAPIException e) {
			System.err.println("Error starting application: " + e.getMessage());
			System.exit(-1);
		}
	}

	public SWRLTab(SWRLAPIApplicationController applicationController) throws SWRLAPIException
	{
		super(APPLICATION_NAME);

		this.rulesView = createAndAddSWRLAPIRulesView(applicationController);
	}

	@Override
	public void update()
	{
		this.rulesView.update();
	}

	@Override
	public String getApplicationName()
	{
		return APPLICATION_NAME;
	}

	private SWRLAPIRulesView createAndAddSWRLAPIRulesView(SWRLAPIApplicationController applicationController)
			throws SWRLAPIException
	{
		Icon ruleEngineIcon = DroolsFactory.getSWRLRuleEngineIcon();
		SWRLAPIRulesView rulesView = new SWRLAPIRulesView(applicationController, ruleEngineIcon);
		Container contentPane = getContentPane();

		contentPane.setLayout(new BorderLayout());
		contentPane.add(rulesView);
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);

		return rulesView;
	}

	@Override
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.setVisible(false);
			System.exit(0);
		}
	}

	private static void Usage()
	{
		System.err.println("Usage: " + SWRLTab.class.getName() + " <owlFileName>");
		System.exit(1);
	}
}
