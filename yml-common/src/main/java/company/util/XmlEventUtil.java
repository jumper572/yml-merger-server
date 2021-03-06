package company.util;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class XmlEventUtil {

    public static Optional<String> getTextOfElement(List<XMLEvent> events, String elementName)
    {
        Optional<Characters> characters = getCharacterEventOfElement(events, elementName);

        if (characters.isPresent())
            return Optional.of(characters.get().getData());

        return Optional.empty();
    }

    public static Optional<Characters> getCharacterEventOfElement(List<XMLEvent> events, String elementName)
    {
        Iterator<XMLEvent> iterator = events.iterator();

        while (iterator.hasNext())
        {
            XMLEvent event = iterator.next();

            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(elementName)) {
                XMLEvent nextEvent = iterator.next();
                if (nextEvent.isCharacters())
                    return Optional.of(nextEvent.asCharacters());
            }

        }

        return Optional.empty();
    }

    public static Optional<XMLEvent> getStartElement(List<XMLEvent> events, String elementName)
    {
        Iterator<XMLEvent> iterator = events.iterator();

        while (iterator.hasNext())
        {
            XMLEvent event = iterator.next();

            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(elementName))
                return Optional.of(event);

        }

        return Optional.empty();
    }

    public static Optional<String> getAttributeValue(List<XMLEvent> events, String element, String attributeName)
    {
        for (XMLEvent event : events) {
            if (!event.isStartElement())
                continue;

            if ( !event.asStartElement().getName().getLocalPart().equals(element) )
                continue;

            if (event.asStartElement().getAttributeByName(QName.valueOf(attributeName)) == null)
                return Optional.empty();

            String attValue = event.asStartElement().getAttributeByName(QName.valueOf(attributeName)).getValue();

            return Optional.of(attValue);
        }

        return Optional.empty();
    }

}
