package uk.org.gtc.api.health;

import com.codahale.metrics.health.HealthCheck;

public class BasicHealthCheck extends HealthCheck
{
    @Override
    protected Result check() throws Exception
    {
        return Result.healthy();
    }

}
