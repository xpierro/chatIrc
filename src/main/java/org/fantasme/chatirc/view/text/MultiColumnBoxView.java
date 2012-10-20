package org.fantasme.chatirc.view.text;

import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

/**
 * Vue en boite multi-colonne.
 */
public class MultiColumnBoxView extends BoxView {
    private MultiColumnTextPane textPane;

    public MultiColumnBoxView(Element element, int i, MultiColumnTextPane textPane) {
        super(element, i);
        this.textPane = textPane;
    }

    protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {

        /*
        * first pass, calculate the preferred sizes
        * and the flexibility to adjust the sizes.
        */
        long preferred = 0;
        int n = getViewCount();
        for (int i = 0; i < n; i++) {
            View v = getView(i);
            spans[i] = (int) v.getPreferredSpan(axis);
            preferred += spans[i];
        }

        textPane.setMinBarPos((int) getView(0).getPreferredSpan(axis));

        /*
         * Second pass, expand or contract by as much as possible to reach
         * the target span. // TODO : AH
         */
        // La target est égale é la width
        // Preferred est la somme des tailles justes des enfants

        // determine the adjustment to be made
        long desiredAdjustment = targetSpan - preferred; // Le "blanc é combler"
        float adjustmentFactor = 0.0f;
        int[] diffs = null;

        if (desiredAdjustment != 0) {
            long totalSpan = 0;
            diffs = new int[n];
            for (int i = 0; i < n; i++) {
                View v = getView(i);
                int tmp;
                if (desiredAdjustment < 0) {
                    tmp = (int) v.getMinimumSpan(axis);
                    diffs[i] = spans[i] - tmp;
                } else {
                    tmp = (int) v.getMaximumSpan(axis);
                    diffs[i] = tmp - spans[i];
                }
                totalSpan += tmp;
            }

            float maximumAdjustment = Math.abs(totalSpan - preferred);
            adjustmentFactor = desiredAdjustment / maximumAdjustment;
            adjustmentFactor = Math.min(adjustmentFactor, 1.0f);
            adjustmentFactor = Math.max(adjustmentFactor, -1.0f);
        }

        // Les calculs importants sont ici:

        int column_2_pos = textPane.getBarPos();
        int column_0_width = spans[0];
        int column_1_width = column_2_pos - column_0_width;
        offsets[1] = column_0_width;
        offsets[2] = Math.max(column_2_pos, column_0_width + spans[1]); 

        spans[0] += offsets[1] - spans[0] - offsets[0];
        if (spans[1] < column_1_width) { // Si assez grand
            spans[1] += offsets[2] - spans[1] - offsets[1];
        }
        spans[2] = getWidth() - (spans[0] + spans[1]);
    }

    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
        int n = getViewCount();
        for (int i = 0; i < n; i++) {
            View v = getView(i);
            int max = (int) v.getMaximumSpan(axis);
            if (max < targetSpan) {
                // can't make the child this wide, align it
                float align = v.getAlignment(axis);
                offsets[i] = (int) ((targetSpan - max) * align);
                spans[i] = max;
            } else {
                // make it the target width, or as small as it can get.
                int min = (int) v.getMinimumSpan(axis);
                offsets[i] = 0;
                spans[i] = Math.max(min, targetSpan);

            }
            offsets[i] = 0;
        }
    }
}