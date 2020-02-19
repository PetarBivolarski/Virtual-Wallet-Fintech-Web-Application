package a16team1.virtualwallet.services;

import a16team1.virtualwallet.models.misc.InspirationalQuote;
import a16team1.virtualwallet.repositories.contracts.InspirationalQuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class InspirationalQuoteServiceImpl implements InspirationalQuoteService {

    private static final int MAX_ID_IN_REPOSITORY = 20;

    private InspirationalQuoteRepository inspirationalQuoteRepository;
    private Random random;

    @Autowired
    public InspirationalQuoteServiceImpl(InspirationalQuoteRepository inspirationalQuoteRepository,
                                         Random random) {
        this.inspirationalQuoteRepository = inspirationalQuoteRepository;
        this.random = random;
    }

    @Override
    public InspirationalQuote getRandom() {
        int randomId = random.nextInt(MAX_ID_IN_REPOSITORY) + 1;
        return inspirationalQuoteRepository.getById(randomId);
    }

    @Override
    public List<InspirationalQuote> getAll() {
        return inspirationalQuoteRepository.getAll();
    }
}
