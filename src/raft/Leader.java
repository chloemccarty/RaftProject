package raft;

import connect.Network;

public class Leader extends Node {
    boolean forfeit = false;

    public Leader(Node node) {
        super(node);
    }

    @Override
    public void handleMessage(Message message) {
        // if term number is greater, immediately relinquish leadership
        if (message == null)
            return;
        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO
        }
        else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // TODO
        }
        else if (message.type == Message.MessageType.REQUEST_VOTES) {
            RequestVote.RequestVoteMessage rvm = (RequestVote.RequestVoteMessage) message.message;

            // candidate's log is greater than the leader
            if (rvm.getTerm() > this.term) {
                // TODO check logs
                forfeit = true;
                this.term = rvm.getTerm();
            }

            // respond to sender
            RequestVoteRespo.RequestVoteResponse.Builder builder = RequestVoteRespo.RequestVoteResponse.newBuilder();
            if (forfeit) {
                builder.setVoteGranted(true);
            } else {
                builder.setVoteGranted(false);
            }
            builder.setTerm(this.term);
            RequestVoteRespo.RequestVoteResponse rvr = builder.build();
            String ip = config.get(rvm.getCandidateId());
            Network.send(Message.MessageType.REQUEST_VOTES_RESPONSE, rvr, ip);
        }
        if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            // shouldn't even get this message. We shouldn't have sent out a ReQuestVotes as a leader
        }
        // will make a call to send response
    }

    @Override
    public Node run() {
        apply();
        // check messages
        // react to messages
        // send response
        Message m = checkForInput();
        if (m != null) {
            handleMessage(m);
            if (forfeit)
                return new Follower(this);
        }


        return this;
    }
}
