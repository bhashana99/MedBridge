package com.medbridge.stack;


import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.Vpc;

public class LocalStack extends Stack {
    private final Vpc vpc;


    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope,id,props);

        this.vpc = createVpc();
    }

    private Vpc createVpc() {
       return Vpc.Builder.create(this,"MedBridgeVPC")
                .vpcName("MedBridgeVPC")
                .maxAzs(2)
                .build();
    }

    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", props);
        app.synth();
    }
}
