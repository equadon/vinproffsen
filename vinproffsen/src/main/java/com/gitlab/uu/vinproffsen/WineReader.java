package com.gitlab.uu.vinproffsen;

import com.gitlab.uu.vinproffsen.db.WineResult;
import com.gitlab.uu.vinproffsen.exceptions.UnknownItemException;
import com.gitlab.uu.vinproffsen.items.Item;
import com.gitlab.uu.vinproffsen.items.ItemFactory;
import com.gitlab.uu.vinproffsen.items.Wine;
import com.gitlab.uu.vinproffsen.items.storage.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Read wines from XML file provided by Systembolaget.
 *
 * @author Niklas Persson
 * @version 2016-03-17
 */
public class WineReader {
    private final static Logger LOG = Logger.getLogger(WineReader.class.getName());

    private final WineModel model;

    private final DocumentBuilderFactory factory;
    private DocumentBuilder builder;

    /**
     * Constructor.
     * @param model WineModel
     * @param filename filename of systembolaget xml file
     */
    public WineReader(WineModel model, String filename) {
        this.model = model;

        LOG.fine("Loading wines from: " + filename);

        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();

            loadItems(filename);
        } catch (ParserConfigurationException | IOException e) {
            LOG.severe(e.getMessage());
        }
    }

    /**
     * Load items from resource.
     * @param resourceName resource name
     * @throws IOException
     */
    private void loadItems(String resourceName) throws IOException {
        WineSettings settings = WineSettings.getInstance();

        Document doc = loadDocument(resourceName);

        NodeList articles = doc.getElementsByTagName("artikel");
        int length = articles.getLength();

        int ignoredItems = 0;
        int n = 1;
        for (int i = 0; i < length; i++) {
            model.send("wine:progress", i, length);

            Element article = (Element) articles.item(i);

            ItemType type = new ItemType(getString(article, "Varugrupp"));

            // Type
            ItemName name = new ItemName(getString(article, "Namn"), getString(article, "Namn2"));

            // Seller info
            String sellStart = getString(article, "Saljstart");
            String area = getString(article, "Ursprung");
            String country = getString(article, "Ursprunglandnamn");
            String producer = getString(article, "Producent");
            String supplier = getString(article, "Leverantor");
            int year = getInteger(article, "Argang");
            int price = (int) (getDouble(article, "Prisinklmoms") * 100);

            ItemSeller seller = new ItemSeller(sellStart, area, country, producer, supplier, year, price);

            // Details
            boolean ecological = getBool(article, "Ekologisk");
            boolean kosher = getBool(article, "Koscher");
            String assortment = getString(article, "Sortiment");
            String description = getString(article, "RavarorBeskrivning");

            ItemDetails details = new ItemDetails(ecological, kosher, assortment, description);

            int volume = (int) getDouble(article, "Volymiml");
            int pricePerLiter = (int) (getDouble(article, "PrisPerLiter") * 100);
            int deposit = (int) (getDouble(article, "Pant") * 100);
            String packaging = getString(article, "Forpackning");
            String seal = getString(article, "Forslutning");
            double alcohol = getDouble(article, "Alkoholhalt") / 100d;

            BeverageDetails beverage = new BeverageDetails(volume, pricePerLiter, deposit, packaging, seal, alcohol);

            // throw away all non-wines and wines that cost more than MAX_PRICE
            if (!type.isWine() || price > settings.getInteger("app.maxPrice")) {
                ignoredItems++;
                continue;
            }

            WineResult queryResult = model.addWine(name, type, seller, details, beverage);
            if (queryResult.count > 0) {
                n++;
            }
        }

        LOG.info("Finished loading wines. Added: " + n + " and ignored: " + ignoredItems);

        model.finished();
    }

    /**
     * Create document from resource.
     * @param resourceName resource
     * @return doc
     * @throws IOException
     */
    private Document loadDocument(String resourceName) throws IOException {
        InputStream is = WineReader.class.getClassLoader().getResourceAsStream(resourceName);
        
        if (is == null) {
            throw new IOException("Unable to locate resource: " + resourceName);
        }

        try {
            return builder.parse(is);
        } catch (SAXException e) {
            return null;
        }
    }

    /**
     * Helper methods to get different data types from element.
     */
    private int getInteger(Element element, String tag) {
        NodeList elements = element.getElementsByTagName(tag);

        if (elements.getLength() > 0) {
            try {
                return Integer.parseInt(elements.item(0).getTextContent());
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private double getDouble(Element element, String tag) {
        NodeList elements = element.getElementsByTagName(tag);

        if (elements.getLength() > 0) {
            try {
                String value = elements.item(0).getTextContent();
                NumberFormat format = NumberFormat.getInstance(Locale.US);

                return format.parse(value).doubleValue();
            } catch (ParseException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private String getString(Element element, String tag) {
        NodeList elements = element.getElementsByTagName(tag);

        if (elements.getLength() > 0) {
            return elements.item(0).getTextContent().trim();
        } else {
            return "";
        }
    }

    private boolean getBool(Element element, String tag) {
        NodeList elements = element.getElementsByTagName(tag);

        return elements.getLength() > 0 && elements.item(0).getTextContent().equals("1");
    }
}
