package com.company;

import company.StAXService;
import company.handlers.xml.XmlEventHandler;
import company.http.*;
import company.providers.ByteArrayXmlEventReaderProvider;
import company.providers.FileXMLEventReaderProvider;
import company.providers.InputStreamXmlEventReaderProvider;
import company.providers.XMLEventReaderProvider;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Naya on 20.01.2016.
 */
public class ModifyService {

    public void process(ModifierConfig config)  {

        byte[] inputXmlBytes = new ModifierXmlBytesProvider(config).get();

        XMLEventReaderProvider readerProvider = new ByteArrayXmlEventReaderProvider(inputXmlBytes, config.getEncoding());

        StAXService stAXService = new StAXService( readerProvider );

        try {
            XmlEventHandler handler = new ModifierXmlEventHandlerProvider(config, readerProvider).get();
            stAXService.process(handler);

        } catch (FileNotFoundException | UnsupportedEncodingException | XMLStreamException e) {
            e.printStackTrace();
        }

    }
}
