package com.gitlab.uu.vinproffsen.ui.views;

import com.gitlab.uu.mvp.Application;
import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.WineModel;
import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.ui.presenters.AddWinePresenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * View that render components on the add new wine tab.
 *
 * @author Niklas Persson
 * @version 2016-03-19
 */
public class AddWineView extends View<WineModel, AddWinePresenter> {
    private final JTextField name;
    private final JTextField type;
    private final JTextField price;
    private final JTextField volume;
    private final JTextField year;
    private final JTextField country;
    private final JTextField area;
    private final JTextField alcohol;
    private final JCheckBox eco;
    private final JCheckBox kosher;

    public AddWineView(Application application, WineModel model) {
        super(application, model);

        name = new JTextField();
        type = new JTextField();
        price = new JTextField();
        volume = new JTextField();
        year = new JTextField();
        country = new JTextField();
        area = new JTextField();
        alcohol = new JTextField();
        eco = new JCheckBox();
        kosher = new JCheckBox();
    }

    public JPanel render() {
        JPanel panel = new JPanel();

        JLabel titleLabel = new JLabel("Lägg till vin");
        titleLabel.setFont(new Font(titleLabel.getFont().getFontName(), Font.BOLD, 24));

        JLabel nameLabel = new JLabel("Namn:");
        JLabel typeLabel = new JLabel("Type:");
        JLabel priceLabel = new JLabel("Pris (SEK):");
        JLabel volumeLabel = new JLabel("Volym (ml):");
        JLabel yearLabel = new JLabel("Årgång:");
        JLabel countryLabel = new JLabel("Land:");
        JLabel areaLabel = new JLabel("Område:");
        JLabel alcoholLabel = new JLabel("Alkoholhalt (%):");
        JLabel ecoLabel = new JLabel("Ekologisk:");
        JLabel kosherLabel = new JLabel("Kosher:");

        JButton saveButton = new JButton(new AbstractAction("Spara") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveWine();
            }
        });

        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addComponent(nameLabel)
                    .addComponent(typeLabel)
                    .addComponent(priceLabel)
                    .addComponent(volumeLabel)
                    .addComponent(yearLabel)
                    .addComponent(countryLabel)
                    .addComponent(areaLabel)
                    .addComponent(ecoLabel)
                    .addComponent(kosherLabel)
                    .addComponent(alcoholLabel)
                    .addComponent(saveButton)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(name)
                    .addComponent(type)
                    .addComponent(price)
                    .addComponent(volume)
                    .addComponent(year)
                    .addComponent(country)
                    .addComponent(area)
                    .addComponent(eco)
                    .addComponent(kosher)
                    .addComponent(alcohol)
            )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(titleLabel)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(nameLabel)
                .addComponent(name)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(typeLabel)
                .addComponent(type)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(priceLabel)
                .addComponent(price)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(volumeLabel)
                .addComponent(volume)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(yearLabel)
                .addComponent(year)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(countryLabel)
                .addComponent(country)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(areaLabel)
                .addComponent(area)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(ecoLabel)
                .addComponent(eco)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(kosherLabel)
                .addComponent(kosher)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(alcoholLabel)
                .addComponent(alcohol)
            )
            .addComponent(saveButton)
        );

        panel.setLayout(layout);

        return panel;
    }

    /**
     * Add wine failed.
     * @param error error message
     */
    public void failed(String error) {
        JOptionPane.showMessageDialog(null, error);
    }

    /**
     * Add wine succeeded.
     * @param result wine result
     */
    public void succeeded(WineResult result) {
        clear();
    }

    /**
     * Save wine to db.
     */
    private void saveWine() {
        presenter.saveWine(name.getText(), type.getText(), price.getText(), volume.getText(), year.getText(),
                country.getText(), area.getText(), alcohol.getText(), eco.isSelected(), kosher.isSelected());
    }

    /**
     * Clear input components.
     */
    private void clear() {
        JTextField[] textFields = new JTextField[] {name, type, price, volume, year, country, area, alcohol};

        for (JTextField field : textFields)
            field.setText("");

        eco.setSelected(false);
        kosher.setSelected(false);
    }

    @Override
    protected AddWinePresenter createPresenter(WineModel model) {
        return new AddWinePresenter(application, this, model);
    }
}
