package a16team1.virtualwallet.services;

import a16team1.virtualwallet.models.misc.InspirationalQuote;

import java.util.List;

public interface InspirationalQuoteService {

    InspirationalQuote getRandom();

     List<InspirationalQuote> getAll();
}
