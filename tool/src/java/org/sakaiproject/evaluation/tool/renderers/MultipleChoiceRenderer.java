/**
 * MultipleChoiceRenderer.java - evaluation - Jan 21, 2008 2:59:12 PM - azeckoski
 * $URL$
 * $Id$
 **************************************************************************
 * Copyright (c) 2008 Centre for Applied Research in Educational Technologies, University of Cambridge
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.sakaiproject.evaluation.tool.renderers;

import org.sakaiproject.evaluation.logic.utils.ArrayUtils;
import org.sakaiproject.evaluation.model.EvalScale;
import org.sakaiproject.evaluation.model.EvalTemplateItem;
import org.sakaiproject.evaluation.model.constant.EvalConstants;
import org.sakaiproject.evaluation.tool.EvaluationConstant;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.UISelectLabel;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.components.decorators.UIStyleDecorator;

/**
 * This handles the rendering of multiple choice type items
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class MultipleChoiceRenderer implements ItemRenderer {

   /**
    * This identifies the template component associated with this renderer
    */
   public static final String COMPONENT_ID = "render-multiplechoice-item:";

   /* (non-Javadoc)
    * @see org.sakaiproject.evaluation.tool.renderers.ItemRenderer#renderItem(uk.org.ponder.rsf.components.UIContainer, java.lang.String, org.sakaiproject.evaluation.model.EvalTemplateItem, int, boolean)
    */
   public UIJointContainer renderItem(UIContainer parent, String ID, String[] bindings, EvalTemplateItem templateItem, int displayNumber, boolean disabled) {
      UIJointContainer container = new UIJointContainer(parent, ID, COMPONENT_ID);

      if (displayNumber <= 0) displayNumber = 0;
      // this if is here because giving an RSF input control a null binding AND a null initial value causes a failure
      String initValue = null;
      if (bindings[0] == null) initValue = "";

      EvalScale scale = templateItem.getItem().getScale();
      String[] scaleOptions = scale.getOptions();
      int optionCount = scaleOptions.length;
      String scaleValues[] = new String[optionCount];
      String scaleLabels[] = new String[optionCount];

      String scaleDisplaySetting = templateItem.getScaleDisplaySetting();
      boolean usesNA = templateItem.getUsesNA().booleanValue();

      if (EvalConstants.ITEM_SCALE_DISPLAY_FULL.equals(scaleDisplaySetting) || 
            EvalConstants.ITEM_SCALE_DISPLAY_VERTICAL.equals(scaleDisplaySetting)) {

         UIBranchContainer fullFirst = UIBranchContainer.make(container, "fullType:");

         for (int count = 0; count < optionCount; count++) {
            scaleValues[count] = new Integer(count).toString();
            scaleLabels[count] = scaleOptions[count];	
         }

         UIOutput.make(fullFirst, "itemNum", displayNumber+"" );
         UIVerbatim.make(fullFirst, "itemText", templateItem.getItem().getItemText());

         // display next row
         UIBranchContainer radiobranchFullRow = UIBranchContainer.make(container, "nextrow:", displayNumber+"");

         String containerId;
         if ( EvalConstants.ITEM_SCALE_DISPLAY_FULL.equals(scaleDisplaySetting) ) {
            containerId = "fullDisplay:";
         } else if ( EvalConstants.ITEM_SCALE_DISPLAY_VERTICAL.equals(scaleDisplaySetting) ) {
            containerId = "verticalDisplay:";
         } else {
            throw new RuntimeException("Invalid scaleDisplaySetting (this should not be possible): " + scaleDisplaySetting);
         }

         UIBranchContainer displayContainer = UIBranchContainer.make(radiobranchFullRow, containerId);

         if (usesNA) {
            scaleValues = ArrayUtils.appendArray(scaleValues, EvaluationConstant.NA_VALUE);
            scaleLabels = ArrayUtils.appendArray(scaleLabels, "");
         }

         UISelect radios = UISelect.make(displayContainer, "dummyRadio", scaleValues, scaleLabels, bindings[0], initValue);
         String selectID = radios.getFullID();

         if (disabled) {
            radios.selection.willinput = false;
            radios.selection.fossilize = false;
         }

         int scaleLength = scaleValues.length;
         int limit = usesNA? scaleLength - 1: scaleLength;  // skip the NA value at the end
         for (int j = 0; j < limit; ++j) {
            UIBranchContainer radiobranchNested = 
               UIBranchContainer.make(displayContainer, "scaleOptions:", j+"");
            UISelectChoice choice = UISelectChoice.make(radiobranchNested, "choiceValue", selectID, j);
            UILabelTargetDecorator.targetLabel(
                  UISelectLabel.make(radiobranchNested, "choiceLabel", selectID, j),
                  choice);
         }

         if (usesNA) {
            UIBranchContainer radiobranch3 = UIBranchContainer.make(displayContainer, "showNA:");
            radiobranch3.decorators = new DecoratorList( new UIStyleDecorator("na") );// must match the existing CSS class				
            UISelectChoice choice = UISelectChoice.make(radiobranch3, "na-input", selectID, scaleLength - 1);
            UILabelTargetDecorator.targetLabel(
                  UIMessage.make(radiobranch3, "na-desc", "viewitem.na.desc"),
                  choice);
         }

      } else {
         throw new IllegalStateException("Unknown scaleDisplaySetting ("+scaleDisplaySetting+") for " + getRenderType());
      }
      return container;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.evaluation.tool.renderers.ItemRenderer#getRenderType()
    */
   public String getRenderType() {
      return EvalConstants.ITEM_TYPE_MULTIPLECHOICE;
   }

}