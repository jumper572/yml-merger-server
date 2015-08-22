package com.company;

import com.company.allowedcategories.IncludedCategoriesProvider;
import com.company.config.Config;
import com.company.factories.handler.*;
import com.company.http.HttpClientProvider;
import com.company.http.HttpService;
import com.company.processing.MergePostProcessor;
import com.company.processing.MergedYmlSource;
import com.company.processing.ReplaceProcessing;
import com.company.readerproviders.FileXMLEventReaderProvider;
import com.company.readerproviders.HttpXMLEventReaderProvider;
import company.Factory;
import company.StAXService;
import company.XMLEventReaderProvider;
import company.handlers.xml.XmlEventHandler;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by user50 on 28.07.2015.
 */
public class MergeService {

    public void process(Config config) throws IOException, XMLStreamException {
        List<XMLEventReaderProvider> readerProviders = new ArrayList<>();

        List<HttpXMLEventReaderProvider> httpProviders = new ArrayList<>();

        if (!config.getUrls().isEmpty()) {
            String psw = new String(Base64.getDecoder().decode(config.getPsw().getBytes()));
            CloseableHttpClient httpClient = new HttpClientProvider(config.getUser(), psw).get();
            HttpService httpService = new HttpService(httpClient);

            for (String url : config.getUrls()) {
                HttpXMLEventReaderProvider provider = new HttpXMLEventReaderProvider(httpService, url, config.getEncoding());

                readerProviders.add(provider);
                httpProviders.add(provider);
            }
        }

        for (String fileName : config.getFiles())
            readerProviders.add(new FileXMLEventReaderProvider(fileName, config.getEncoding()));

        byte[] bytes = new MergedYmlSource(config, readerProviders).provide();


        ReplaceProcessing processing = new ReplaceProcessing(config.getEncoding(), config.getReplaces());
        bytes = processing.process(bytes);

        new MergePostProcessor(config.getEncoding(), config.getCurrencies(), config.getOutputFile()).process(bytes);

        for (HttpXMLEventReaderProvider httpProvider : httpProviders) {
            httpProvider.removeTmpFile();
        }
    }


}
