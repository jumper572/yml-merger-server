package com.company.factories.handler;

import com.company.config.Replace;
import company.Currency;
import company.DataProvider;
import company.Factory;
import company.conditions.OfferBelongToCategories;
import company.conditions.TrueCondition;
import company.handlers.aggregated.*;
import company.handlers.xml.AggregatedXmlEventNotifier;
import company.handlers.xml.CurrentPriceNameProvider;
import company.handlers.xml.SuccessiveXmlEventHandler;
import company.handlers.xml.XmlEventHandler;
import company.handlers.xml.currency.RenameElementNameHandler;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OfferHandlerFactory implements Factory<XmlEventHandler> {

    XMLEventWriter out;
    Set<String> allowedCategories;

    public OfferHandlerFactory( XMLEventWriter out, Set<String> allowedCategories) {
        this.out = out;
        this.allowedCategories = allowedCategories;
    }

    @Override
    public XmlEventHandler get() {
        List<XmlEventHandler> handlers = new ArrayList<>();

        List<AggregatedXmlEventHandler> offersHandlers = new ArrayList<>();
        offersHandlers.add(new AggregatedXmlEventWriter(out, new OfferBelongToCategories(allowedCategories)));

        handlers.add(new AggregatedXmlEventNotifier(new SuccessiveAggregatedEventHandler(offersHandlers), "offer"));

        return new SuccessiveXmlEventHandler(handlers);
    }
}
