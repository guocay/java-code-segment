package elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * elastic search 客户端
 * @author GuoCay
 * @since 2023/3/2
 */
public class ElasticSearchDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchDemo.class);

    public static void main(String[] args) throws IOException {
        RestClient client = RestClient.builder(new HttpHost("localhost", 9200)).build();
        ElasticsearchTransport transport = new RestClientTransport(client, new JacksonJsonpMapper());
        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        SearchResponse<String> result = esClient.search(search -> search.index("aaa"), String.class);

		result.hits().hits().forEach(hit -> LOGGER.info(hit.source()));
    }
}
