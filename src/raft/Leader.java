package raft;

import client.Client;
import connect.Network;

import java.util.HashMap;

public class Leader extends Node {
    boolean forfeit = false;
    int nextIndex[];

    int matchIndex[];

    public Leader(Node node) {
        super(node);

        //Initiate matchIndex and nextIndex. Create arrays corresponding to ip address. So with each ip address sent, we have
        //a corresponding place in the array. Could we use a hashmap?

        //Could we make this an arrayList?
        matchIndex = new int[numNodes];
        for (int i=0; i<numNodes; i++) {
            //Initialized to zero for each index, which corresponds to each node
            matchIndex[i] = -1;
        }

        //Store a nextIndex value for each node in the cluster. If nextIndex does not match the matchIndex value for a node, we must decrement
        //matchIndex and send another message to that follower. If follower resturns false, decrement again. Continue this until terms match,
        //then delete all previous log entries.
        nextIndex = new int[numNodes];
        for (int i=0; i<numNodes; i++) {
            //Initialized to leader's last log index +1, for each index, which corresponds to each node
            nextIndex[i] = this.log.size();
        }
        System.out.println("Initializing as leader...");
    }

    @Override
    public void handleMessage(Message message) {
        // TODO add client message handling!

        // if term number is greater, immediately relinquish leadership
        if (message == null)
            return;
        if (message.type == Message.MessageType.APPEND_ENTRIES) {
            // TODO
            AppendEntries.AppendEntriesMessage ae = (AppendEntries.AppendEntriesMessage) message.message;
            if (ae.getTerm() > this.term) forfeit = true;
        }
        else if (message.type == Message.MessageType.APPEND_ENTRIES_RESPONSE) {
            // TODO
            AppendEntries.Response aer = (AppendEntries.Response) message.message;
            if (aer.getSuccess()) {
                //increment nextIndex and set matchIndex equal to nextIndex for the corresponding follower from which the message was received.
                int index = nextIndex[aer.getFollowerId()];
                nextIndex[aer.getFollowerId()] = index+1;
                //update matchIndex to one less than the updated nextIndex value.
                matchIndex[aer.getFollowerId()] = index;
            }

            else{
                //Decrement nextIndex and keep matchIndex the same.
                int index = nextIndex[aer.getFollowerId()];
                nextIndex[aer.getFollowerId()] = index-1;
            }
        }
        else if (message.type == Message.MessageType.REQUEST_VOTES) {
            respondToRequestVote(message);
        }
        if (message.type == Message.MessageType.REQUEST_VOTES_RESPONSE) {
            // shouldn't even get this message. We shouldn't have sent out a ReQuestVotes as a leader
        }
    }

    @Override
    public Node run() {
        while (true) {
            // Handle updating commit index within the apply method
            apply();
            commit();

            // check messages
            // react to messages
            // send response
            Message m = checkForInput();
            if (m != null) {
                System.out.println("Message received by leader");
                handleMessage(m);
                if (forfeit) {
                    return new Follower(this);
                }
            }

            sendHeartbeats();

        }
    }


    public void commit() {
        if (log.size() <= commitIndex+1) {
            //skip and do nothing
            return;
        }

        int N = commitIndex+1;
        for (int i=0; i<numNodes; i++) {
            while ( N < log.size() && matchIndex[i] != N) {
                N++;
            }

            if (log.get(matchIndex[i]).term == term)
                break;
        }

        // Cycle through matchIndex array to see if N value is less than a majority of the nodes' index values.
        // If true, then update commitIndex of leader to N.
        int count=0;
        for (int i=0; i<numNodes; i++) {
            if (matchIndex[i]>=N) count++;
        }

        if (count > numNodes/2)  {
            //Set commitIndex equal to N
            commitIndex = N;
        }
    }


    public void sendHeartbeats() {
        while (true) {
            long elapsed = 0;
            long beg = System.currentTimeMillis();
            while (elapsed <= 100) {
                long end = System.currentTimeMillis();
                elapsed = end-beg;
            }

            // Send messages/heartbeat
            // If last log index >= nextIndex for a follower, send appendEntries with index starting at nextIndex
            AppendEntries.AppendEntriesMessage.Builder builder = AppendEntries.AppendEntriesMessage.newBuilder();
            builder.setTerm(term);
            builder.setLeaderId(id);
            builder.setPrevLogIndex(log.size()-1);
            if (log.size() == 0)
                builder.setPrevLogTerm(-1);
            else
                builder.setPrevLogTerm(log.get(log.size()-1).term);
            builder.setLeaderCommit(commitIndex);

            // we need to send an empty message
            AppendEntries.AppendEntriesMessage.Entry.Builder entryBuilder = AppendEntries.AppendEntriesMessage.Entry.newBuilder();
            entryBuilder.setMessage("");
            AppendEntries.AppendEntriesMessage.Entry entry = entryBuilder.build();
            builder.addEntries(lastApplied, entry);

           AppendEntries.AppendEntriesMessage message = builder.build();

           for (int i = 0; i < numNodes-1; i++) {
               Network.send(Message.MessageType.APPEND_ENTRIES, message, config.get(i));
           }


        }

    }

}
