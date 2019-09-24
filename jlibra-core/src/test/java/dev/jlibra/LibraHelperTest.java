package dev.jlibra;

import static org.bouncycastle.util.encoders.Hex.encode;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import java.security.PrivateKey;
import java.security.Security;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.ByteString;

import dev.jlibra.admissioncontrol.query.AccountData;
import dev.jlibra.admissioncontrol.transaction.ImmutableProgram;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import types.AccountStateBlobOuterClass.AccountStateBlob;
import types.AccountStateBlobOuterClass.AccountStateWithProof;
import types.GetWithProof.GetAccountStateResponse;

public class LibraHelperTest {

    private static final String PRIVATE_KEY_HEX = "3051020101300506032b6570042204202b1115484c64c297179d4ec8aa660f09eeae900a1ba6f16423f82869a101c8e98121002e00f50d1ba024895c72a92cee1310dfafefcc826629c266a4c80b914772f82d";
    private static final String ACCOUNT_STATE_HEX = "010000002100000001217da6c6b3e19f1825cfb2676daecce3bf3de03cf26647c78df00b371b25cc978d000000200000006674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c5451210100000000000200000000000000200000000577b70d3d2631ba9babe7d05a135f5cc1cbc3d8cfd0a6fc67ad0b56c4fb4cb201000000000000002000000059f1557694ba7d04f578f9db390dfa894ff9bfb66ed12b6263e72fdccb1d9c220300000000000000";

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testSignTransaction() {
        PrivateKey privateKey = KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX);
        String signature = new String(
                encode(LibraHelper.signTransaction(ImmutableTransaction.builder()
                        .expirationTime(1L)
                        .gasUnitPrice(1L)
                        .maxGasAmount(1L)
                        .program(ImmutableProgram.builder().code(ByteString.copyFrom(new byte[] { 1 })).build())
                        .senderAccount(new byte[] { 1 })
                        .sequenceNumber(1L)
                        .build(), privateKey)));

        assertThat(signature, is(
                "3d11514c47b128ecbdb84a4619d622c5425eaf83f9b1e1b4d32e672fa06df9048b125d04cace580b04d1b63fafd8a9e448affb94665a1019f178432954fc1a0d"));
    }

    @Test
    public void testReadAccountStates() {
        List<AccountData> accountStates = LibraHelper
                .readAccountStates(GetAccountStateResponse.newBuilder().setAccountStateWithProof(AccountStateWithProof
                        .newBuilder().setBlob(
                                AccountStateBlob.newBuilder()
                                        .setBlob(ByteString.copyFrom(Hex.decode(ACCOUNT_STATE_HEX.getBytes())))
                                        .build())
                        .build()).build());

        assertThat(accountStates, is(iterableWithSize(1)));
        assertThat(new String(encode(accountStates.get(0).getAccountAddress())),
                is("6674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c"));
        assertThat(accountStates.get(0).getBalanceInMicroLibras(), is(18960724L));
        assertThat(accountStates.get(0).getReceivedEvents().getCount(), is(2));
        assertThat(accountStates.get(0).getSentEvents().getCount(), is(1));
        assertThat(accountStates.get(0).getSequenceNumber(), is(3));
        assertThat(accountStates.get(0).getDelegatedWithdrawalCapability(), is(false));
    }
}
