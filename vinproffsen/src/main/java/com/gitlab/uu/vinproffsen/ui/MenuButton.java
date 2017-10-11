package com.gitlab.uu.vinproffsen.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Buttons used for the main menu. Customized toggle buttons with icons.
 */
public class MenuButton extends JToggleButton {
    private final ImageIcon defaultIcon;
    private final ImageIcon selectedIcon;

    public MenuButton(ImageIcon icon, String title, String tooltip) {
        this.selectedIcon = icon;
        this.defaultIcon = createGreyIcon(icon);

        setFocusPainted(false);
        setToolTipText(tooltip);

        if (defaultIcon == null || selectedIcon == null) {
            setText(title);
        } else {
            setIcon(defaultIcon);
            setPressedIcon(defaultIcon);
            setSelectedIcon(selectedIcon);
        }
    }

    /**
     * Apply a greyscale filter for non-selected buttons.
     * @param icon icon to greyscale
     * @return icon
     */
    private ImageIcon createGreyIcon(ImageIcon icon) {
        Image img = icon.getImage();
        BufferedImage image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        // apply greyscale filter
        int height = image.getHeight(null);
        int width = image.getWidth(null);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                if (!(red == 0 && green == 0 && blue == 0)) {
                    int avg = (red + green + blue) / 3;
                    image.setRGB(x, y, new Color(avg, avg, avg, alpha).getRGB());
                }
            }
        }

        return new ImageIcon(image);
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected)
            setIcon(selectedIcon);
        else
            setIcon(defaultIcon);
    }
}
