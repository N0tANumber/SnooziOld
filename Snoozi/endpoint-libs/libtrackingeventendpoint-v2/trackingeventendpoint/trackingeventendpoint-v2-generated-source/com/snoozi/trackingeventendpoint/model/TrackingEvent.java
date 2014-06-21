/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-06-09 16:41:44 UTC)
 * on 2014-06-21 at 13:58:03 UTC 
 * Modify at your own risk.
 */

package com.snoozi.trackingeventendpoint.model;

/**
 * Model definition for TrackingEvent.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the trackingeventendpoint. For a detailed explanation
 * see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class TrackingEvent extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer androidVersion;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String apkVersion;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String description;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String deviceInformation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long key;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("string")
  private java.lang.String trackingeventendpointString;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String timeString;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long timestamp;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String type;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String userid;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long videoid;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getAndroidVersion() {
    return androidVersion;
  }

  /**
   * @param androidVersion androidVersion or {@code null} for none
   */
  public TrackingEvent setAndroidVersion(java.lang.Integer androidVersion) {
    this.androidVersion = androidVersion;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getApkVersion() {
    return apkVersion;
  }

  /**
   * @param apkVersion apkVersion or {@code null} for none
   */
  public TrackingEvent setApkVersion(java.lang.String apkVersion) {
    this.apkVersion = apkVersion;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDescription() {
    return description;
  }

  /**
   * @param description description or {@code null} for none
   */
  public TrackingEvent setDescription(java.lang.String description) {
    this.description = description;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDeviceInformation() {
    return deviceInformation;
  }

  /**
   * @param deviceInformation deviceInformation or {@code null} for none
   */
  public TrackingEvent setDeviceInformation(java.lang.String deviceInformation) {
    this.deviceInformation = deviceInformation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getKey() {
    return key;
  }

  /**
   * @param key key or {@code null} for none
   */
  public TrackingEvent setKey(java.lang.Long key) {
    this.key = key;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getString() {
    return trackingeventendpointString;
  }

  /**
   * @param trackingeventendpointString trackingeventendpointString or {@code null} for none
   */
  public TrackingEvent setString(java.lang.String trackingeventendpointString) {
    this.trackingeventendpointString = trackingeventendpointString;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTimeString() {
    return timeString;
  }

  /**
   * @param timeString timeString or {@code null} for none
   */
  public TrackingEvent setTimeString(java.lang.String timeString) {
    this.timeString = timeString;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp timestamp or {@code null} for none
   */
  public TrackingEvent setTimestamp(java.lang.Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getType() {
    return type;
  }

  /**
   * @param type type or {@code null} for none
   */
  public TrackingEvent setType(java.lang.String type) {
    this.type = type;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUserid() {
    return userid;
  }

  /**
   * @param userid userid or {@code null} for none
   */
  public TrackingEvent setUserid(java.lang.String userid) {
    this.userid = userid;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getVideoid() {
    return videoid;
  }

  /**
   * @param videoid videoid or {@code null} for none
   */
  public TrackingEvent setVideoid(java.lang.Long videoid) {
    this.videoid = videoid;
    return this;
  }

  @Override
  public TrackingEvent set(String fieldName, Object value) {
    return (TrackingEvent) super.set(fieldName, value);
  }

  @Override
  public TrackingEvent clone() {
    return (TrackingEvent) super.clone();
  }

}
