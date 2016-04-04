package com.company;

import com.company.providers.ModifierXmlEventHandlerProvider;
import com.company.providers.SplitXmlEventHandlerProvider;
import company.StAXService;
import company.handlers.xml.XmlEventHandler;
import company.http.*;
import company.providers.FileXMLEventReaderProvider;
import company.providers.XMLEventReaderProvider;
import company.stream.*;
import company.stream.storage.InMemoryStorage;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Naya on 20.01.2016.
 */
public class ModifyService {

    public void process(ModifierConfig config)  {

        if (config.getInputFileURL()!=null && !config.getInputFileURL().isEmpty()){

            try {
                CloseableHttpClient httpClient;
                try {
                    httpClient = new HttpClientProvider(config.getUser(), config.getPsw()).get();
                } catch (Exception e){
                    e.printStackTrace();
                    throw new RuntimeException("Unable to get HttpClient");
                }
                HttpService httpService = new HttpService(httpClient);
                HttpRequestProvider requestProvider = new DownloadPriceListRequest(config.getInputFileURL());
                HttpResponseHandler<String> responseHandler = new SaveIntoFileHttpResponseHandler(config.getEncoding());
                config.setInputFile(httpService.execute(requestProvider, responseHandler));
            } catch (IOException e) {
                throw new RuntimeException("Unable to do auto merge of " + config.getId(), e);
            }
        }

        try {
            InputStreamOperator modify = new XmlInputStreamOperator(config.getEncoding(), new ModifierXmlEventHandlerProvider(config).get(), new InMemoryStorage());
            InputStreamOperator replace = new ReplaceFragmentsOperator(config.getEncoding(), config.getReplaces());

            InputStream modifiedXmlFile = modify.andThen(replace).apply(new FileInputStream(config.getInputFile()));

            XmlInputStreamConsumer splitAndStore = new XmlInputStreamConsumer(config.getEncoding(), new SplitXmlEventHandlerProvider(config).get());
            splitAndStore.accept(modifiedXmlFile);
        } catch (FileNotFoundException | UnsupportedEncodingException | XMLStreamException e) {
            throw new RuntimeException(e);
        }

    }
}
