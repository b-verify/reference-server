package integrationtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import crpyto.CryptographicDigest;
import server.BVerifyServer;
import server.StartingData;

public class BVerifyServerTest {
	private static final Logger logger = Logger.getLogger(BVerifyServerTest.class.getName());
	private static final byte[] START_VALUE = CryptographicDigest.hash("STARTING".getBytes());

	private static StartingData STARTING_DATA;
	
	@BeforeClass
	public static void setup(){
		int nClients = 10;
		int nClientsPerAdsMax = 3;
		int nADS = 100;
		STARTING_DATA = new StartingData(nClients, nClientsPerAdsMax, nADS, START_VALUE);
	}
	
	@Test
	public void testSingleADSUpdatesEveryEntryOnce() {
		int batchSize = 1;
		boolean requireSignatures = true;
		BVerifyServer server = new BVerifyServer(STARTING_DATA, batchSize, requireSignatures);
		MockTester tester = new MockTester(STARTING_DATA, server, batchSize, requireSignatures);
		List<byte[]> adsIds = tester.getADSIds();
		logger.log(Level.INFO, "testing updating each entry once, total updates : "+adsIds.size());
		byte[] newValue = CryptographicDigest.hash("some new value".getBytes());
		for(byte[] adsId : adsIds) {
			boolean updateAccepted = tester.doUpdate(adsId, newValue);
			Assert.assertTrue("Update should be accepted", updateAccepted);
			boolean proofsValid = tester.getAndCheckProofsAllADSIds();
			Assert.assertTrue("Proofs should be valid", proofsValid);
		}
	}
	
	@Test
	public void testSingleADSUpdatesEveryEntryOnceBatched() {
		// batch size is now 25!
		int batchSize = 25;
		boolean requireSignatures = true;
		BVerifyServer server = new BVerifyServer(STARTING_DATA, batchSize, requireSignatures);
		MockTester tester = new MockTester(STARTING_DATA, server, batchSize, requireSignatures);
		List<byte[]> adsIds = tester.getADSIds();
		logger.log(Level.INFO, "testing updating each entry once, total updates : "+adsIds.size());
		byte[] newValue = CryptographicDigest.hash("some new value".getBytes());
		for(byte[] adsId : adsIds) {
			boolean updateAccepted = tester.doUpdate(adsId, newValue);
			Assert.assertTrue("Update should be accepted", updateAccepted);
			boolean proofsValid = tester.getAndCheckProofsAllADSIds();
			Assert.assertTrue("Proofs should be valid", proofsValid);
		}
	}
	
	@Test
	public void testSingleADSUpdatesMultipleTimes() {
		int batchSize = 1;
		boolean requireSignatures = true;
		BVerifyServer server = new BVerifyServer(STARTING_DATA, batchSize, requireSignatures);
		MockTester tester = new MockTester(STARTING_DATA, server, batchSize, requireSignatures);
		List<byte[]> adsIds = tester.getADSIds();
		List<byte[]> adsIdsToUpdate = new ArrayList<>(adsIds);
		adsIdsToUpdate.addAll(new ArrayList<>(adsIds));
		adsIdsToUpdate.addAll(new ArrayList<>(adsIds));
		logger.log(Level.INFO, "testing updates multiple times, total updates: "+adsIdsToUpdate.size());
		Collections.shuffle(adsIdsToUpdate);
		int i = 0;
		for(byte[] adsId : adsIdsToUpdate) {
			byte[] newValue = CryptographicDigest.hash(("some new value"+i).getBytes());
			boolean updateAccepted = tester.doUpdate(adsId, newValue);
			Assert.assertTrue("Update should be accepted", updateAccepted);
			boolean proofsValid = tester.getAndCheckProofsAllADSIds();
			Assert.assertTrue("Proofs should be valid", proofsValid);
			i++;
		}
	}
	
	@Test
	public void testUpdateMultipleTimesBatched() {
		int batchSize = 25;
		boolean requireSignatures = true;
		BVerifyServer server = new BVerifyServer(STARTING_DATA, batchSize, requireSignatures);
		MockTester tester = new MockTester(STARTING_DATA, server, batchSize, requireSignatures);
		List<byte[]>adsIds = tester.getADSIds();
		List<byte[]> adsIdsToUpdate = new ArrayList<>();
		// 300 updates done in 25 update batches
		for(int i = 0; i < 3; i++) {
			// we only shuffle within a batch
			// NOTE IF a batch updates the same adsId multiple times,
			// the server does not make any ordering guarantees
			// we need to make sure that this doesn't happen 
			// for ease of testing
			Collections.shuffle(adsIds);
			adsIdsToUpdate.addAll(new ArrayList<>(adsIds));
		}
		logger.log(Level.INFO, "testing updates multiple times, total updates: "+adsIdsToUpdate.size());
		int i = 0;
		for(byte[] adsId : adsIdsToUpdate) {
			byte[] newValue = CryptographicDigest.hash(("some new value"+i).getBytes());
			boolean updateAccepted = tester.doUpdate(adsId, newValue);
			Assert.assertTrue("Update should be accepted", updateAccepted);
			boolean proofsValid = tester.getAndCheckProofsAllADSIds();
			Assert.assertTrue("Proofs should be valid", proofsValid);
			i++;
		}
	}
	
	@Test
	public void testMultipleADSUpdates() {
		int batchSize = 1;
		boolean requireSignatures = true;
		BVerifyServer server = new BVerifyServer(STARTING_DATA, batchSize, requireSignatures);
		MockTester tester = new MockTester(STARTING_DATA, server, batchSize, requireSignatures);
		List<byte[]> adsIds = tester.getADSIds();
		// updates
		List<Map.Entry<byte[], byte[]>> updates = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			byte[] newValue = CryptographicDigest.hash(("some new value"+i).getBytes());
			updates.add(Map.entry(adsIds.get(i), newValue));
		}
		boolean updateAccepted = tester.doUpdate(updates);
		Assert.assertTrue("Update should be accepted", updateAccepted);
		boolean proofsValid = tester.getAndCheckProofsAllADSIds();
		Assert.assertTrue("Proofs should be valid", proofsValid);	
	}
	
	@Test
	public void testMultipleADSUpdatesMultipleUpdates() {
		int batchSize = 1;
		boolean requireSignatures = true;
		BVerifyServer server = new BVerifyServer(STARTING_DATA, batchSize, requireSignatures);
		MockTester tester = new MockTester(STARTING_DATA, server, batchSize, requireSignatures);
		List<byte[]> adsIds = tester.getADSIds();
		
		List<Map.Entry<byte[], byte[]>> updates = new ArrayList<>();
		final int updateSize = 5;
		int update = 0;
		int salt = 0;
		for(byte[] adsId : adsIds) {
			byte[] newValue = CryptographicDigest.hash(("some new value"+salt).getBytes());
			updates.add(Map.entry(adsId, newValue));
			update++;
			salt++;
			if(updateSize == update) {
				boolean updateAccepted = tester.doUpdate(updates);
				Assert.assertTrue("Update should be accepted", updateAccepted);
				boolean proofsValid = tester.getAndCheckProofsAllADSIds();
				Assert.assertTrue("Proofs should be valid", proofsValid);	
				update = 0;
				updates.clear();
			}			
		}
		
	}	
	
}
