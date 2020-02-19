package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.misc.InspirationalQuote;

import java.util.List;

public interface InspirationalQuoteRepository {

    InspirationalQuote getById(int id);

    List<InspirationalQuote> getAll();
}
