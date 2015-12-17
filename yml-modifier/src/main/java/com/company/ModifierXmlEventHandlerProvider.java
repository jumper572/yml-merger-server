package com.company;

import com.company.functions.ReplaceWordsOperator;
import com.company.handlers.OffersCategoryIdModifier;
import com.company.handlers.OffersSeparator;
import com.company.handlers.ProgressHandler;
import com.company.operators.AddContentToDescription;
import com.company.operators.ModifyOfferDescription;
import com.company.operators.OfferDescriptionProvider;
import company.StAXService;
import company.conditions.*;
import company.handlers.xml.*;
import company.handlers.xml.buffered.AddElementIfAbsent;
import company.handlers.xml.buffered.BufferXmlEventOperator;
import company.handlers.xml.buffered.BufferedXmlEventHandler;
import company.handlers.xml.buffered.ComplexBufferXmlEventOperator;
import company.providers.FileXMLEventReaderProvider;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class ModifierXmlEventHandlerProvider {

    ModifierConfig config;

    private XMLEventFactory xmlEventFactory = XMLEventFactory.newFactory();

    public ModifierXmlEventHandlerProvider(ModifierConfig config) {
        this.config = config;
    }

    public XmlEventHandler get() throws FileNotFoundException, UnsupportedEncodingException, XMLStreamException {
        List<XmlEventHandler> handlers = new ArrayList<>();
        if (config.isModifyOfferId())
            handlers.add(new AttributeValueModifier("offer","id", (old)-> config.getOfferIdPrefix()+old));

        if (config.isModifyCategoryId()) {
            handlers.add(new AttributeValueModifier("category", "id", (old) -> config.getCategoryIdPrefix() + old));
            handlers.add(new AttributeValueModifier("category", "parentId", (old) -> config.getCategoryIdPrefix() + old));
            handlers.add(new OffersCategoryIdModifier(config.getCategoryIdPrefix()));
        }

        handlers.add(new ModifyElementAttributes("offer", new OfferAttributeModificator(xmlEventFactory) ));
        handlers.add(new XmlEventFilter(new ModifyTextData((old) -> old.equals("0") ? "10000" : old ), new InElementCondition("price")));

        TreeSet<String> forbiddenWords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        forbiddenWords.add("опт");
        forbiddenWords.add("опт.");
        forbiddenWords.add("оптом");

        handlers.add(new XmlEventFilter(new ModifyTextData(new ReplaceWordsOperator(forbiddenWords)), new InElementCondition("description").or(new InElementCondition("name"))));

        handlers.add(getOutputHandler());

        XmlEventHandler handler = new SuccessiveXmlEventHandler(handlers);

        List<BufferXmlEventOperator> operators = new ArrayList<>();

        if (config.isModifyDescription())
            operators.add(provideDescriptionModification());

        operators.add(new AddElementIfAbsent("categoryId", xmlEventFactory, Optional.of("0")));
        operators.add(new AddElementIfAbsent("currencyId", xmlEventFactory, Optional.of("RUR")));
        operators.add(new AddElementIfAbsent("description", xmlEventFactory, Optional.empty()));
        operators.add(new AddElementIfAbsent("name", xmlEventFactory, Optional.of("Удалено")));
        operators.add(new AddElementIfAbsent("model", xmlEventFactory, Optional.of("Удалено")));
        operators.add(new AddElementIfAbsent("price", xmlEventFactory, Optional.of("10000")));
        operators.add(new AddElementIfAbsent("url", xmlEventFactory, Optional.of("http://kupi-na-dom.ru")));
        operators.add(new AddElementIfAbsent("vendor", xmlEventFactory, Optional.empty()));

        handler = new BufferedXmlEventHandler(handler, new StartElement("offer"), new EndElement("offer"), new ComplexBufferXmlEventOperator(operators) );

        return handler;
    }

    private XmlEventHandler getOutputHandler() throws XMLStreamException {
        int offerCount = getOfferCount();

        List<XmlEventHandler> fileXmlEventWriters = new ArrayList<>();
        Predicate<XMLEvent> closeCondition = (event) -> event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("yml_catalog");
        for (int i = 0; i < config.getFilesCount(); i++)
            fileXmlEventWriters.add(new WriteEventToFile(config.getOutputDir() + "/output"+i+".xml", config.getEncoding(), closeCondition));

        List<XmlEventHandler> handlers = new ArrayList<>();
        for (XmlEventHandler fileXmlEventWriter : fileXmlEventWriters)
            handlers.add(new XmlEventFilter(fileXmlEventWriter, new InElementCondition("offers").negate()));

        handlers.add(new XmlEventFilter(new OffersSeparator( fileXmlEventWriters, offerCount/config.getFilesCount() ), new InElementCondition("offers") ));
        handlers.add(new ProgressHandler(offerCount));

        return new SuccessiveXmlEventHandler(handlers);
    }

    private BufferXmlEventOperator provideDescriptionModification()
    {
        OfferDescriptionProvider provider = new OfferDescriptionProvider(config.getTemplate(),"utf-8");

        List<BufferXmlEventOperator> operators = new ArrayList<>();
        operators.add(new AddElementIfAbsent("description",xmlEventFactory, Optional.<String>empty()));
        operators.add(new ModifyOfferDescription(provider));
        operators.add(new AddContentToDescription(provider, xmlEventFactory.newFactory()));

        return new ComplexBufferXmlEventOperator(operators);
    }

    private int getOfferCount() throws XMLStreamException {
        EventCounter eventCounter = new EventCounter(new InElementCondition("offers").and((event) -> event.isStartElement()
                && event.asStartElement().getName().getLocalPart().equals("offer")));

        new StAXService(new FileXMLEventReaderProvider(config.getInputFile(), config.getEncoding())).process(eventCounter);

        return eventCounter.getCount();
    }



}
