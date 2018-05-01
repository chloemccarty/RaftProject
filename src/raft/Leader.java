package raft;

import connect.Network;

import java.util.HashMap;

public class Leader extends Node {
    boolean forfeit = false;
    //int nextIndex[];

    //HashMap of ip address and index
    HashMap<String, Integer> nextIndex;

   //int matchIndex[];

    //HashMap of ip address and index
    HashMap<String, Integer> matchIndex;

    public Leader(Node node) {
        super(node);

        //Initiate matchIndex and nextIndex. Create arrays corresponding to ip address. So with each ip address sent, we have
        //a corresponding place in the array. Could we use a hashmap?

        //Could we make this an arrayList?
        matchIndex = new HashMap();
        for (int i=0; i<numNodes; i++) {
            //Initialized to zero for each index, which corresponds to each node
            matchIndex.put(config.get(i), 0);
        }

        //Store a nextIndex value for each node in the cluster. If nextIndex does not match the matchIndex value for a node, we must decrement
        //matchIndex and send another message to that follower. If follower resturns false, decrement again. Continue this until terms match,
        //then delete all previous log entries.
        nextIndex = new HashMap();
        for (int i=0; i<numNodes; i++) {
            //Initialized to leader's last log index +1, for each index, which corresponds to each node
            nextIndex.put(config.get(i), this.log.size());
        }
        System.out.println("Initializing as leader...");
    }

    @Override
    public void handleMessage(Message message) {
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
                this.matchIndex.get(Network.getIP) //Get the corresponding nextIndex value, increment as we have succeeded in appending,
                                              //then replace that hash index with the updated one.
            }

            else{
                //Decrement nextIndex and keep matchIndex the same.
                this.matchIndex.get
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
            apply();
            // check messages
            // react to messages
            // send response
            Message m = checkForInput();
            if (m != null) {
                System.out.println("Message received by leader");
                handleMessage(m);
                if (forfeit)
                    return new Follower(this);
            }

        }
    }
}
