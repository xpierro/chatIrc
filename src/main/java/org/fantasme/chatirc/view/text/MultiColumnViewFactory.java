package org.fantasme.chatirc.view.text;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import org.fantasme.chatirc.model.text.MultiColumnDocument;

/**
 * Usine à vue multi-colonne.
 */
public class MultiColumnViewFactory implements ViewFactory {
    private MultiColumnTextPane textPane;

    public MultiColumnViewFactory(MultiColumnTextPane textPane) {
        this.textPane = textPane;
    }

    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new LabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new ParagraphView(elem);
            } else if (kind.equals(MultiColumnDocument.HOUR)) {
                return new ParagraphView(elem);
            } else if (kind.equals(MultiColumnDocument.ID)) {
                return new ParagraphView(elem) {
                    public short getRightInset() {
                        return 5;
                    }
                };
            } else if (kind.equals(MultiColumnDocument.M_CONTENT)) {
                return new ParagraphView(elem) {
                    public short getLeftInset() {
                        return 5;
                    }
                };
            } else if (kind.equals(MultiColumnDocument.MESSAGE)) {
                return new MultiColumnBoxView(elem, BoxView.X_AXIS, textPane);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, BoxView.Y_AXIS) {
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

                        /*
                               * Second pass, expand or contract by as much as possible to reach
                               * the target span.
                               */

                        // determine the adjustment to be made
                        long desiredAdjustment = targetSpan - preferred;
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

                        // make the adjustments
                        int totalOffset = 0;
                        for (int i = 0; i < n; i++) {
                            offsets[i] = totalOffset;
                            if (desiredAdjustment != 0) {
                                float adjF = adjustmentFactor * diffs[i];
                                //spans[i] += Math.round(adjF);
                            }
                            totalOffset = (int) Math.min((long) totalOffset + (long) spans[i], Integer.MAX_VALUE);
                        }
                    }

                };
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        }

        // default to text display
        return new LabelView(elem);
    }

}
