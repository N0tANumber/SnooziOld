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
 * (build: 2014-04-15 19:10:39 UTC)
 * on 2014-05-29 at 05:04:31 UTC 
 * Modify at your own risk.
 */

package com.snoozi.videoendpoint.model;

/**
 * Model definition for Video.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the videoendpoint. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Video extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String description;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer level;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer like;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String status;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long timestamp;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String url;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long userid;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer viewcount;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDescription() {
    return description;
  }

  /**
   * @param description description or {@code null} for none
   */
  public Video setDescription(java.lang.String description) {
    this.description = description;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Video setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getLevel() {
    return level;
  }

  /**
   * @param level level or {@code null} for none
   */
  public Video setLevel(java.lang.Integer level) {
    this.level = level;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getLike() {
    return like;
  }

  /**
   * @param like like or {@code null} for none
   */
  public Video setLike(java.lang.Integer like) {
    this.like = like;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getStatus() {
    return status;
  }

  /**
   * @param status status or {@code null} for none
   */
  public Video setStatus(java.lang.String status) {
    this.status = status;
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
  public Video setTimestamp(java.lang.Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrl() {
    return url;
  }

  /**
   * @param url url or {@code null} for none
   */
  public Video setUrl(java.lang.String url) {
    this.url = url;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getUserid() {
    return userid;
  }

  /**
   * @param userid userid or {@code null} for none
   */
  public Video setUserid(java.lang.Long userid) {
    this.userid = userid;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getViewcount() {
    return viewcount;
  }

  /**
   * @param viewcount viewcount or {@code null} for none
   */
  public Video setViewcount(java.lang.Integer viewcount) {
    this.viewcount = viewcount;
    return this;
  }

  @Override
  public Video set(String fieldName, Object value) {
    return (Video) super.set(fieldName, value);
  }

  @Override
  public Video clone() {
    return (Video) super.clone();
  }

}
