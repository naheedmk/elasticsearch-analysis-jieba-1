package org.elasticsearch.index.analysis;

import java.io.File;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

import com.huaban.analysis.jieba.WordDictionary;

public class JiebaTokenFilterFactory extends AbstractTokenFilterFactory {
	private final ESLogger log = Loggers
			.getLogger(JiebaTokenFilterFactory.class);
	private String type;
	private File configFile;

	@Inject
	public JiebaTokenFilterFactory(Index index,
			@IndexSettings Settings indexSettings, @Assisted String name,
			@Assisted Settings settings) {
		super(index, indexSettings, name, settings);
		type = settings.get("seg_mode", "index");
		Environment env = new Environment(indexSettings);
		configFile = env.configFile();
		WordDictionary.getInstance().init(
				new File(configFile, "jieba").toPath());
	}

	@Override
	public TokenStream create(TokenStream input) {
		return new JiebaTokenFilter(type, input);
	}

}
