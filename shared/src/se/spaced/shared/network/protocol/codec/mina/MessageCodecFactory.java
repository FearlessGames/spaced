package se.spaced.shared.network.protocol.codec.mina;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


@Singleton
public class MessageCodecFactory implements ProtocolCodecFactory {
	private final ProtocolDecoder messageDecoder;
	private final ProtocolEncoder messageEncoder;


	@Inject
	public MessageCodecFactory(ProtocolDecoder messageDecoder, ProtocolEncoder messageEncoder) {
		this.messageDecoder = messageDecoder;
		this.messageEncoder = messageEncoder;

	}

	public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
		return messageEncoder;
	}

	public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
		return messageDecoder;
	}
}