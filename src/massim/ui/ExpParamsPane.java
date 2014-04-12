/**
 * L2FProd Common v9.2 License.
 *
 * Copyright 2005 - 2009 L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package massim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import massim.ui.config.ConfigLoader;
import massim.ui.config.ConfigProperty;
import massim.ui.config.ConfigProperty.InputType;
import massim.ui.config.ExperimentConfiguration;

/**
 * PropertySheetPage. <br>
 * 
 */
public class ExpParamsPane extends JPanel {

	private static final long serialVersionUID = -8056166229378542059L;
	int WIDTH = 350;
	private LinkedList<Component> paraComponents;
	private ExperimentConfiguration expConfig;
	JPanel pnlParams;
	public ExpParamsPane(ExperimentConfiguration mExpConfig) {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		StyleSet.setBorder(this, 0, 2, 0, 0);
		setBackground(Color.WHITE);
		
		JPanel pnlBoxLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		pnlBoxLabel.setBackground(getBackground());
		pnlBoxLabel.setMaximumSize(new Dimension(WIDTH, 25));
		add(pnlBoxLabel);
		
		JLabel lblTitle = new JLabel("Simulation Parameters");
		lblTitle.setHorizontalAlignment(JLabel.LEFT);
		StyleSet.setEmptyBorder(lblTitle, 5);
		StyleSet.setTitleFont(lblTitle);
		pnlBoxLabel.add(lblTitle);
		
		JScrollPane scrollPane = new JScrollPane();
		StyleSet.setEmptyBorder(scrollPane, 0);
		add(scrollPane);
		
		pnlParams = new JPanel();
		pnlParams.setLayout(new GridLayout(0, 2, 0, 10));
		pnlParams.setBackground(Color.white);
		StyleSet.setEmptyBorder(pnlParams, 5);
		scrollPane.setViewportView(pnlParams);
		setConfiguration(mExpConfig);
	}
	
	public void setConfiguration(ExperimentConfiguration mExpConfig)
	{
		expConfig = mExpConfig;
		pnlParams.removeAll();
		paraComponents = ConfigLoader.loadPropertiesInPanel(expConfig, pnlParams, WIDTH, null);
		pnlParams.revalidate();
		pnlParams.repaint();
	}
	
	public ExperimentConfiguration getExpConfig()
	{
		expConfig.updateConfigForParams(paraComponents);
		return expConfig;
	}
}