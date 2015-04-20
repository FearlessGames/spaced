package se.fearless.bender;

import com.google.common.collect.Lists;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;
import org.jibble.pircbot.IrcException;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.bender.commands.Command;
import se.fearless.bender.commands.CommitCommand;
import se.fearless.bender.commands.CrashCommand;
import se.fearless.bender.commands.FailedCommand;
import se.fearless.bender.commands.GitCommitCommand;
import se.fearless.bender.commands.GithubCommand;
import se.fearless.bender.commands.ShutdownCommand;
import se.fearless.bender.commands.StatusCommand;
import se.fearless.bender.commands.SuccessCommand;
import se.fearless.bender.commands.WaveCommand;
import se.fearless.bender.services.TinyUrlService;
import se.fearless.bender.services.WGet;
import se.fearless.bender.services.rss.ForumsRssItemFormatter;
import se.fearless.bender.services.rss.GitHubRssItemFormatter;
import se.fearless.bender.services.rss.JiraRssItemFormatter;
import se.fearless.bender.services.rss.RssChannelWrapper;
import se.fearless.bender.services.rss.RssService;
import se.fearless.bender.services.rss.StoryIrcPoster;
import se.fearless.bender.statistics.MessageStatisticsService;
import se.fearlessgames.common.log.Slf4jJulBridge;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Bender implements Container {
	private static final Logger log = LoggerFactory.getLogger(Bender.class);
	private static final String SHUTDOWN = "shutdown";
	private static final String FAILED = "failed";
	private static final String SUCCESS = "success";
	private static final String COMMIT = "commit";
	private static final String STATUS = "status";
	private static final String GITHUB = "github";
	private static final String CRASH = "crash";
	private static final String PIVOTAL = "pivotal";
	private static final String WAVE = "wave";
	private static final String GIT_COMMIT = "gitcommit";


	private final IrcAnnouncer ircAnnouncer;

	private final SocketConnection connection;
	private final Map<String, Command> commands = new HashMap<String, Command>();
	private final BuildState buildState;
	private final CommitLog commitLog;
	private final MessageStatisticsService messageStatisticsService;

	private final TinyUrlService urlService;

	public static void main(String[] args) {
		Slf4jJulBridge.init();

		String channel = "#sr-mmo-dev";

		if (args.length > 0) {
			channel = args[0];
		}

		log.info("Starting bender for channel " + channel);
		try {
			IrcAnnouncer ircAnnouncer = new IrcAnnouncer("Spaced-Bender", channel);
			ircAnnouncer.connect("se.quakenet.org");
			ircAnnouncer.joinChannel(channel);
			Bender bender = new Bender(ircAnnouncer);
			bender.start();
		} catch (IOException e) {
			log.error("IOException in bender, quiting", e);
		} catch (IrcException e) {
			log.error("IrcException in bender, quiting", e);
		}
	}

	private void start() throws IOException {
		int port = 8090;
		SocketAddress address = new InetSocketAddress(port);
		connection.connect(address);
		log.info("Bender connected to " + address);
	}

	public Bender(IrcAnnouncer ircAnnouncer) throws IOException {
		this.ircAnnouncer = ircAnnouncer;
		connection = new SocketConnection(this);

		this.urlService = new TinyUrlService(new WGet());

		try {

			RssChannelWrapper jira = new RssChannelWrapper(FeedParser.parse(new ChannelBuilder(),
					new URL(
							"http://flexo.fearlessgames.se/jira/plugins/servlet/streams?os_username=Demazia&os_password=akkurat")),
					new StoryIrcPoster(ircAnnouncer, new JiraRssItemFormatter("Jira")));

			RssChannelWrapper forums = new RssChannelWrapper(FeedParser.parse(new ChannelBuilder(),
					new URL(
							"http://forums.fearlessgames.se/smartfeed.php?u=1001&e=mdGhWRxX8W8.&lastvisit=1&limit=1_HOUR&sort_by=user&feed_type=RSS2.0&feed_style=COMPACT")),
					new StoryIrcPoster(ircAnnouncer, new ForumsRssItemFormatter("Forums")));

			RssChannelWrapper fearlessRender = new RssChannelWrapper(FeedParser.parse(new ChannelBuilder(),
					new URL("https://github.com/thehiflyer/fearless-render/commits/master.atom")),
					new StoryIrcPoster(ircAnnouncer, new GitHubRssItemFormatter("fearless-render", urlService)));

			RssService rssService = new RssService(Lists.newArrayList(jira, forums, fearlessRender));
			rssService.start();
		} catch (ParseException e) {
			log.error("Failed to parse for the rss service", e);
		}

		buildState = new BuildState();
		commitLog = new CommitLog(100);
		messageStatisticsService = new MessageStatisticsService();
		ircAnnouncer.setCommitLog(commitLog);
		ircAnnouncer.setMessageStatisticsService(messageStatisticsService);
		registerCommands(ircAnnouncer, connection);
	}

	private void registerCommands(IrcAnnouncer ircAnnouncer, SocketConnection connection) {
		commands.put(SHUTDOWN, new ShutdownCommand(ircAnnouncer, connection));
		commands.put(FAILED, new FailedCommand(ircAnnouncer, buildState));
		commands.put(SUCCESS, new SuccessCommand(ircAnnouncer, buildState));
		commands.put(STATUS, new StatusCommand(ircAnnouncer, buildState));
		commands.put(COMMIT, new CommitCommand(ircAnnouncer, buildState, commitLog, urlService));
		commands.put(GITHUB, new GithubCommand(ircAnnouncer));
		commands.put(CRASH, new CrashCommand(ircAnnouncer));
		commands.put(WAVE, new WaveCommand(ircAnnouncer));
		commands.put(GIT_COMMIT, new GitCommitCommand(commitLog, buildState, ircAnnouncer, urlService));
	}

	@Override
	public void handle(Request request, Response response) {
		PrintStream body = null;
		try {
			body = response.getPrintStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long time = System.currentTimeMillis();

		response.set("Content-Type", "text/plain");
		response.set("Server", "HelloWorld/1.0 (Simple 4.0)");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);

		body.println("Hello World");
		body.close();

		String type = getType(request);

		Command command = commands.get(type);
		if (command != null) {
			command.execute(request);
		}
	}

	private String getType(final Request request) {
		try {
			final String type = request.getParameter("type");
			if (type != null) {
				return type;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return request.getPath().toString().replaceAll("/", "");
	}
}
