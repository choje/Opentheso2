package iiif;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.digitalcollections.iiif.model.MetadataEntry;
import de.digitalcollections.iiif.model.enums.ViewingHint;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.PropertyValue;

import java.util.Locale;


@Ignore
public class IiifConnexion {

    @Test
    public void iffTest() throws JsonProcessingException {


        Canvas canvas = new Canvas("1122334455");
        canvas.addLabel("A label");
        canvas.addDescription("Firas TEST.");
        canvas.setWidth(800);
        canvas.setHeight(600);

        //https://gallica.bnf.fr/iiif/ark:/12148/btv1b9055204k/f1/full/1500,750/0/native.jpg

        // Image
        canvas.addIIIFImage("https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg/1280px-La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg", ImageApiProfile.LEVEL_ONE);

        // Thumbnail
        ImageContent thumbnail = new ImageContent("https://gallica.bnf.fr/iiif/foo/full/250,/0/default.jpg");
        thumbnail.addService(new ImageService("https://gallica.bnf.fr/iiif/foo", ImageApiProfile.LEVEL_ONE));
        canvas.addThumbnail(thumbnail);

        // Other Content
        canvas.addSeeAlso(new OtherContent("https://gallica.bnf.fr/ocr/foo.hocr", "text/html"));

        // Search Service
        //ContentSearchService searchService = new ContentSearchService("http://some.uri/search/foo");
        //searchService.addAutocompleteService("http://some.uri/autocomplete/foo");
        //canvas.addService(searchService);

        // Metadata
        canvas.addMetadata("Author", "FIRAS GABSI");
        canvas.addMetadata("Location", "PAris");
        PropertyValue key = new PropertyValue();
        key.addValue(Locale.ENGLISH, "Key");
        key.addValue(Locale.GERMAN, "Schlüssel");
        key.addValue(Locale.CHINESE, "钥");
        PropertyValue value = new PropertyValue();
        value.addValue(Locale.ENGLISH, "A value", "Another value");
        value.addValue(Locale.GERMAN, "Ein Wert", "Noch ein Wert");
        value.addValue(Locale.CHINESE, "值", "另值");
        canvas.addMetadata(new MetadataEntry(key, value));

        // Other stuff
        canvas.addViewingHint(ViewingHint.NON_PAGED);

        // Licensing/Attribution
        canvas.addLicense("http://rightsstatements.org/vocab/NoC-NC/1.0/");
        canvas.addAttribution("Some fictional institution");
        canvas.addLogo("https://gallica.bnf.fr/logo.jpg");
        canvas.addLogo(new ImageContent(new ImageService(
                "https://gallica.bnf.fr/iiif/logo", ImageApiProfile.LEVEL_ONE)));

        String json = new IiifObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(canvas);
        System.out.println(">> " + json);
    }

}
